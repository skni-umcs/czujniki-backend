package skni.kamilG.skin_sensors_api.Scheduler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import skni.kamilG.skin_sensors_api.Sensor.Model.Sensor;
import skni.kamilG.skin_sensors_api.Sensor.Service.SensorUpdateService;

@Service
@Slf4j
@RequiredArgsConstructor
public class Scheduler {
  private final Map<Short, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
  private final SensorUpdateService sensorUpdateService;
  private final TaskScheduler taskScheduler;

  @Value("${scheduler.default-rate:180}")
  private short defaultRate;

  @PostConstruct
  public void init() {
    startDefaultTasks();
  }

  private void startDefaultTasks() {
    List<Sensor> activeSensors = sensorUpdateService.findSensorsToUpdate();
    updateTaskRates(activeSensors);
    log.info("Started {} default sensor tasks", activeSensors.size());
  }

  public void updateTaskRates(List<Sensor> sensorsToUpdate) {
    sensorsToUpdate.forEach(
        sensor -> {
          ScheduledFuture<?> existingTask = scheduledTasks.get(sensor.getId());
          if (existingTask != null) {
            existingTask.cancel(false);
            log.debug("Cancelled existing task for sensor {}", sensor.getId());
          }
          short rate = Optional.ofNullable(sensor.getRefreshRate()).orElse(defaultRate);
          ScheduledFuture<?> newTask =
              taskScheduler.scheduleAtFixedRate(
                  () -> updateSingleSensor(sensor), Duration.ofSeconds(rate));
          scheduledTasks.put(sensor.getId(), newTask);
          log.debug("Scheduled task for sensor {} with rate {} seconds", sensor.getId(), rate);
        });
  }

  private void updateSingleSensor(Sensor sensor) {
    try {
      sensorUpdateService.updateSingleSensor(sensor);
      log.debug("Completed update for sensor {}", sensor.getId());
    } catch (Exception e) {
      log.error("Failed to update sensor {}: {}", sensor.getId(), e.getMessage());
    }
  }

  @PreDestroy
  public void shutdown() {
    scheduledTasks.values().forEach(task -> task.cancel(false));
    scheduledTasks.clear();
    log.info("Cleaned up all scheduled tasks");
  }
}
