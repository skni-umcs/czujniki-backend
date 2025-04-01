package skni.kamilG.skin_sensors_api.Admin.Service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skni.kamilG.skin_sensors_api.Admin.DTO.RefreshRateCommand;
import skni.kamilG.skin_sensors_api.Admin.DTO.SensorCommand;
import skni.kamilG.skin_sensors_api.Admin.DTO.StatusCommand;
import skni.kamilG.skin_sensors_api.Scheduler.Scheduler;
import skni.kamilG.skin_sensors_api.Sensor.Exception.SensorNotFoundException;
import skni.kamilG.skin_sensors_api.Sensor.Model.Sensor;
import skni.kamilG.skin_sensors_api.Sensor.Repository.SensorRepository;

@Slf4j
@Service
@AllArgsConstructor
public class AdminService implements IAdminService {

  private final SensorRepository sensorRepository;
  private final Scheduler scheduler;

  /**
   * Generic method to process any type of SensorCommand and update the appropriate field in the
   * Sensor.
   *
   * @param commands List of commands implementing SensorCommand
   * @param <T> Type of command implementing SensorCommand
   */
  @Transactional
  public <T extends SensorCommand> void updateSensors(List<T> commands) {
    List<Sensor> sensorsToUpdate =
        commands.stream()
            .map(
                command ->
                    sensorRepository
                        .findById(command.sensorId())
                        .map(
                            sensor -> {
                              if (command instanceof RefreshRateCommand refreshRateCommand) {
                                sensor.setRefreshRate(refreshRateCommand.refreshRate());
                                log.info(
                                    "Updating sensor {} refresh rate to {} seconds",
                                    sensor.getId(),
                                    refreshRateCommand.refreshRate());
                              } else if (command instanceof StatusCommand statusCommand) {
                                sensor.setStatus(statusCommand.status());
                                log.info(
                                    "Updating sensor {} status to {}",
                                    sensor.getId(),
                                    statusCommand.status());
                              }
                              return sensor;
                            })
                        .orElseThrow(() -> new SensorNotFoundException(command.sensorId())))
            .collect(Collectors.toList());

    List<Sensor> savedSensors = sensorRepository.saveAll(sensorsToUpdate);
    scheduler.updateTaskRates(savedSensors);
    log.info("Updated {} sensors", savedSensors.size());
  }
}
