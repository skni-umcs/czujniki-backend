package skni.kamilG.skin_sensors_api.Controller;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import skni.kamilG.skin_sensors_api.Model.Sensor.DTO.SensorResponse;
import skni.kamilG.skin_sensors_api.Model.User;
import skni.kamilG.skin_sensors_api.Service.IUserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final IUserService userService;

  @PostMapping("/me/favorites/{sensorId}")
  public ResponseEntity<Void> addFavoriteSensor(
      @PathVariable @NotNull Short sensorId, @AuthenticationPrincipal UserDetails userDetails) {

    Long userId = userService.getUserIdByUsername(userDetails.getUsername());
    userService.addFavoriteSensor(userId, sensorId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/me/favorites/{sensorId}")
  public ResponseEntity<Void> removeFavoriteSensor(
      @PathVariable Short sensorId, @AuthenticationPrincipal UserDetails userDetails) {

    Long userId = userService.getUserIdByUsername(userDetails.getUsername());
    userService.removeFavoriteSensor(userId, sensorId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/me")
  public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
    User user = userService.getUserByUsername(userDetails.getUsername());
    return ResponseEntity.ok(user);
  }

  @GetMapping("/me/favorites")
  public ResponseEntity<List<SensorResponse>> getCurrentUserFavorites(
      @AuthenticationPrincipal UserDetails userDetails) {
    Long userId = userService.getUserIdByUsername(userDetails.getUsername());
    List<SensorResponse> favoriteSensors = userService.getFavoriteSensors(userId);
    return ResponseEntity.ok(favoriteSensors);
  }
}
