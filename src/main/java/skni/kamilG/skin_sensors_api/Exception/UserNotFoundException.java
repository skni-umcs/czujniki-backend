package skni.kamilG.skin_sensors_api.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException() {
    super("User not found");
  }

  public UserNotFoundException(String username) {
    super(String.format("User not found with name: %s", username));
  }

  public UserNotFoundException(Long userId) {
    super(String.format("User not found with ID: %d", userId));
  }
}
