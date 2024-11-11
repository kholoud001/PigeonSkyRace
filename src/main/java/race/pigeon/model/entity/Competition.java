package race.pigeon.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.time.LocalDate;


@Setter
@Getter
@Document(collection = "competitions")
public class Competition {
    @Id
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private LocalDate departureTime;
    //private double distance;
    private int pigeonCount;
    private int percentage;
    private boolean status;
    private String releasePlace;

    @DBRef
    private List<Pigeon> pigeons;

    @DBRef
    private appUser user;

    public Competition(){}


}
