package race.pigeon.entities.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document(collection = "users")
public class User {

    @Id
    private int id;
    private String name;
    private String email;
    private String role;


    public User(){}


}