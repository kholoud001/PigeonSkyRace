package race.pigeon.entities.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import race.pigeon.entities.enums.Role;

@Setter
@Getter
@Document(collection = "users")
public abstract class User {

    @Id
    protected int id;
    protected String name;
    protected String email;
    protected Role role;

    public User(){}


}