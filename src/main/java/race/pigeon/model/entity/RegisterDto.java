package race.pigeon.model.entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDto {

    @NotEmpty
    private String username;
    @NotEmpty
    private String loftName;
    private double latitude;
    private double longitude;
    @NotEmpty
    @Size(min = 6, message = "Minimum Password length is 6 characters")
    private String password;

}
