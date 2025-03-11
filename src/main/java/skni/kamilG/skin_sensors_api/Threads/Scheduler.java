package skni.kamilG.skin_sensors_api.Threads;

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
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;
import skni.kamilG.skin_sensors_api.Sensor.Model.Sensor;
import skni.kamilG.skin_sensors_api.Sensor.Service.SensorUpdateService;

@Service
@Slf4j
@RequiredArgsConstructor
public class Scheduler {
  private final Map<Short, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
  private final Executor virtualThreadExecutor;
  private final SensorUpdateService sensorUpdateService;
  private TaskScheduler taskScheduler;

  @Value("${scheduler.thread-pool-size:65}")
  private int threadPoolSize;

  @Value("${scheduler.default-rate:180}")
  private short defaultRate;

  @PostConstruct
  public void init() {
    ScheduledExecutorService executorService =
        Executors.newScheduledThreadPool(threadPoolSize, Thread.ofVirtual().factory());
    taskScheduler = new ConcurrentTaskScheduler(executorService);
    log.info("Initialized scheduler with {} threads", threadPoolSize);
    startDefaultTasks();
  }

  private void startDefaultTasks() {
    List<Sensor> activeSensors = sensorUpdateService.findSensorsToUpdate();
    updateTaskRates(activeSensors);
    log.info(
        "Started {} default sensor tasks with rate {} seconds", activeSensors.size(), defaultRate);
  }

  public void updateTaskRates(List<Sensor> sensorsToUpdate) {
    sensorsToUpdate.forEach(
        (sensor) -> {
          ScheduledFuture<?> newTask =
              taskScheduler.scheduleAtFixedRate(
                  () -> updateSingleSensor(sensor),
                  Duration.ofSeconds(
                      Optional.ofNullable(sensor.getRefreshRate()).orElse(defaultRate)));
          scheduledTasks.put(sensor.getId(), newTask);
        });
  }

  private void updateSingleSensor(Sensor sensor) {
    CompletableFuture.runAsync(
        () -> {
          try {
            sensorUpdateService.updateSingleSensor(sensor);
            log.debug("Completed update for sensor {}", sensor.getId());
          } catch (Exception e) {
            log.error("Failed to update sensor {}: {}", sensor.getId(), e.getMessage());
          }
        },
        virtualThreadExecutor);
  }

  @PreDestroy
  public void shutdown() {
    scheduledTasks.values().forEach(task -> task.cancel(false));
    scheduledTasks.clear();
    log.info("Cleaned up all scheduled tasks");
  }
}
