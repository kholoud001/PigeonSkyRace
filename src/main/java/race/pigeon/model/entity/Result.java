package race.pigeon.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Setter
@Getter
@Document(collection = "results")
public class Result {

        @Id
        private String id;
        private Date heureArrivee;
        private double distance;
        private double vitesse;
        private double point;

        @DBRef
        private Pigeon pigeon;

        @DBRef
        private Competition competition;

        public Result(){}


}
