package race.pigeon.model.entity;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "results")
public class Result {

    @Id
    private String id;

    @NotNull(message = "Arrival date cannot be null")
    private LocalDateTime heureArrivee;

    private double distance;
    private double vitesse;
    private double point;
    private int ranking;

    @DBRef
    private Pigeon pigeon;

    @DBRef
    private Competition competition;

}
