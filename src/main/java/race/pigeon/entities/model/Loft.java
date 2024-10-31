package race.pigeon.entities.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Document(collection = "lofts")
public class Loft {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private double latitude;
    private double longitude;

    @DBRef
    private List<Pigeon> pigeons;

    @DBRef
    private Breeder breeder;


    public Loft() {}

    @Override
    public String toString() {
        return "Loft{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", pigeons=" + pigeons +
                ", breeder=" + breeder +
                '}';
    }

}
