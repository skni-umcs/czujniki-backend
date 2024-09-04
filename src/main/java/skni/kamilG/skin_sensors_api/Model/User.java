package skni.kamilG.skin_sensors_api.Model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String username;

  private String password;

  @ManyToMany
  @JoinTable(
      name = "users_favorite_sensors",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "sensor_id"))
  private Set<Sensor> favoriteSensors = new HashSet<>();
}
