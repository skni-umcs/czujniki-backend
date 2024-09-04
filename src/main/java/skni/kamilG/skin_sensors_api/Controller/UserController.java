package skni.kamilG.skin_sensors_api.Controller;

import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import skni.kamilG.skin_sensors_api.Model.Sensor;
import skni.kamilG.skin_sensors_api.Model.User;
import skni.kamilG.skin_sensors_api.Service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  // Add to favourites
  @PostMapping("/{userId}/favorites/{sensorId}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Void> addFavoriteSensor(
      @PathVariable Long userId, @PathVariable Short sensorId) {
    userService.addFavoriteSensor(userId, sensorId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  // Remove from favourites
  @DeleteMapping("/{userId}/favorites/{sensorId}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Void> removeFavoriteSensor(
      @PathVariable Long userId, @PathVariable Short sensorId) {
    userService.removeFavoriteSensor(userId, sensorId);
    return ResponseEntity.noContent().build();
  }

  // Profile Info
  @GetMapping("/me")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<User> getCurrentUser(Principal principal) {
    User user = userService.getUserByUsername(principal.getName());
    return ResponseEntity.ok(user);
  }

  // Profile's favourites
  @GetMapping("/me/favorites")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<Sensor>> getCurrentUserFavorites(Principal principal) {
    User user = userService.getUserByUsername(principal.getName());
    List<Sensor> favoriteSensors = userService.getFavoriteSensors(user.getId());
    return ResponseEntity.ok(favoriteSensors);
  }
}
