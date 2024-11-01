package race.pigeon.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import race.pigeon.model.enums.Role;

import java.util.List;

@Setter
@Getter
@Document(collection = "users")
public class User {

    @Id
    protected String id;

    @Indexed(unique = true)
    protected String username;

    protected String password;

    @Indexed(unique = true)
    private String loftName;

    private double latitude;
    private double longitude;

    private Role role;

    @DBRef
    private List<Pigeon> pigeons;

    public User(){}

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", loftName='" + loftName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", pigeons=" + pigeons +
                ", role=" + role +
                '}';
    }


}