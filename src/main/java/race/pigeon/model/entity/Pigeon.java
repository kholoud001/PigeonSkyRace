package race.pigeon.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@Document(collection = "pigeons")
public class Pigeon {
    @Id
    private String id;

    @Indexed(unique = true)
    private String ringNumber;

    private String sex;
    private int age;
    private String color;
    private String image;

    @DBRef
    private appUser user;

    @DBRef
    private List<Competition> competition;

    public Pigeon() {}

    @Override
    public String toString() {
        return "Pigeon{" +
                "id='" + id + '\'' +
                ", ringNumber='" + ringNumber + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                ", color='" + color + '\'' +
                ", image='" + image + '\'' +
                ", breeder=" + user +
                ", competition=" + competition +
                '}';
    }
}
