package race.pigeon.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "competitions")
public class Competition {
    @Id
    private String id;

    private String name;
    private double latitude;
    private double longitude;
    private LocalDateTime departureTime;
    private int pigeonCount;
    private int percentage;
    private boolean status;
    private String releasePlace;

    @DBRef
    private List<Pigeon> pigeons;

    @DBRef
    private appUser user;

    }
