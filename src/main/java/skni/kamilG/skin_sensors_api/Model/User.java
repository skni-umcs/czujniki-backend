package skni.kamilG.skin_sensors_api.Model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import skni.kamilG.skin_sensors_api.Model.Sensor.Sensor;

@NoArgsConstructor
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
      name = "user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @ManyToMany
  @JoinTable(
      name = "user_favorite_sensors",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "sensor_id"))
  private Set<Sensor> favoriteSensors = new HashSet<>();
}
