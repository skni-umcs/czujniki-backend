package skni.kamilG.skin_sensors_api.Admin;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import skni.kamilG.skin_sensors_api.Admin.Command.RefreshRateCommand;
import skni.kamilG.skin_sensors_api.Admin.Command.StatusCommand;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/sensor/")
@AllArgsConstructor
public class SensorAdminController {

  private final IAdminService adminSensorService;

  @PatchMapping("/batch/refresh-rates")
  public ResponseEntity<Void> changeRefreshRateBatch(
      @Valid @RequestBody List<RefreshRateCommand> sensorsRequestToChangeRateUpdates) {
    adminSensorService.updateSensors(sensorsRequestToChangeRateUpdates);
    log.info(
        "Received request to update refresh rates for {} sensors",
        sensorsRequestToChangeRateUpdates.size());
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/batch/status")
  public ResponseEntity<Void> changeStatusSingleSensor(
      @Valid @RequestBody List<StatusCommand> sensorsRequestToChangeStatusChangeRequests) {
    adminSensorService.updateSensors(sensorsRequestToChangeStatusChangeRequests);
    log.info(
        "Received request to update status for {} sensors",
        sensorsRequestToChangeStatusChangeRequests.size());
    return ResponseEntity.ok().build();
  }
}
