package race.pigeon.entities.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Document(collection = "breeders")
public class Breeder extends User{

    @DBRef//one to one
    private Loft loft;

    public Breeder() {
        super();
    }


    @Override
    public String toString() {
        return "Breeder{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", role='" + getRole() + '\'' +
                ", loft=" + loft +
                '}';
    }
}
