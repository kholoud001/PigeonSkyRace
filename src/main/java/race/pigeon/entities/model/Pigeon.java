package race.pigeon.entities.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@Document(collection = "pigeons")
public class Pigeon {
    @Id
    private String id;
    private String ringNumber;
    private String sex;
    private int age;
    private String color;
    private String image;

    @DBRef
    private Loft loft;

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
                ", loft='" + loft + '\'' +
                ", competition='" + competition + '\'' +
                '}';
    }
}
