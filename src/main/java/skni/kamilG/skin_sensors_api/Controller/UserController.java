package skni.kamilG.skin_sensors_api.Controller;

// TODO user experience implementation
// @RestController
// @RequestMapping("/api/users")
// @RequiredArgsConstructor
// public class UserController {
//
//  private final IUserService userService;
//
//  @PostMapping("/me/favorites/{sensorId}")
//  public ResponseEntity<Void> addFavoriteSensor(
//      @PathVariable @NotNull Short sensorId, @AuthenticationPrincipal UserDetails userDetails) {
//
//    Long userId = userService.getUserIdByUsername(userDetails.getUsername());
//    userService.addFavoriteSensor(userId, sensorId);
//    return ResponseEntity.status(HttpStatus.CREATED).build();
//  }
//
//  @DeleteMapping("/me/favorites/{sensorId}")
//  public ResponseEntity<Void> removeFavoriteSensor(
//      @PathVariable Short sensorId, @AuthenticationPrincipal UserDetails userDetails) {
//
//    Long userId = userService.getUserIdByUsername(userDetails.getUsername());
//    userService.removeFavoriteSensor(userId, sensorId);
//    return ResponseEntity.noContent().build();
//  }
//
//  @GetMapping("/me")
//  public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
//    User user = userService.getUserByUsername(userDetails.getUsername());
//    return ResponseEntity.ok(user);
//  }
//
//  @GetMapping("/me/favorites")
//  public ResponseEntity<List<SensorResponse>> getCurrentUserFavorites(
//      @AuthenticationPrincipal UserDetails userDetails) {
//    Long userId = userService.getUserIdByUsername(userDetails.getUsername());
//    List<SensorResponse> favoriteSensors = userService.getFavoriteSensors(userId);
//    return ResponseEntity.ok(favoriteSensors);
//  }
// }
