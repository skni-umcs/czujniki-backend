package skni.kamilG.skin_sensors_api.Model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DateRange {
    private LocalDateTime start;
    private LocalDateTime end;
}
