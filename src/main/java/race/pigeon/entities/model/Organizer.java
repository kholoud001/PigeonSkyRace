package race.pigeon.entities.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Document(collection = "organizers")
public class Organizer extends User {

    @DBRef
    private List<Competition> competitions;

    public Organizer(){
        super();
    }

    @Override
    public String toString() {
        return "Organizer{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", role='" + getRole() + '\'' +
                ", competitions='" + getCompetitions() + '\'' +

                '}';
    }

}
