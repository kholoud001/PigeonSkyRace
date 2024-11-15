package race.pigeon.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDto {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;

}
