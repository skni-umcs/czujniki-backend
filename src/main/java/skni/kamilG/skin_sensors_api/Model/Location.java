package skni.kamilG.skin_sensors_api.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "location")
public class Location {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private short id;

  private double latitude;
  private double longitude;
  private String facultyName;
}
