package skni.kamilG.skin_sensors_api.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.User;
import skni.kamilG.skin_sensors_api.Service.IUserService;
import skni.kamilG.skin_sensors_api.Service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final IUserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  // Add to favourites
  @PostMapping("/me/favorites/{sensorId}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Void> addFavoriteSensor(
      @PathVariable Short sensorId, @AuthenticationPrincipal UserDetails userDetails) {

    Long userId = userService.getUserIdByUsername(userDetails.getUsername());
    userService.addFavoriteSensor(userId, sensorId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  // Remove from favourites
  @DeleteMapping("/me/favorites/{sensorId}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Void> removeFavoriteSensor(
      @PathVariable Short sensorId, @AuthenticationPrincipal UserDetails userDetails) {

    Long userId = userService.getUserIdByUsername(userDetails.getUsername());
    userService.removeFavoriteSensor(userId, sensorId);
    return ResponseEntity.noContent().build();
  }

  // Profile Info
  @GetMapping("/me")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
    User user = userService.getUserByUsername(userDetails.getUsername());
    return ResponseEntity.ok(user);
  }

  // Profile's favourites
  @GetMapping("/me/favorites")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<Sensor>> getCurrentUserFavorites(
      @AuthenticationPrincipal UserDetails userDetails) {
    Long userId = userService.getUserIdByUsername(userDetails.getUsername());
    List<Sensor> favoriteSensors = userService.getFavoriteSensors(userId);
    return ResponseEntity.ok(favoriteSensors);
  }
}
