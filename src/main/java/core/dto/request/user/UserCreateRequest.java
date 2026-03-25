package core.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequest {

    @NotBlank(message = "FIELD_IS_NOT_EMPTY")
    private String username;

    @NotBlank(message = "FIELD_IS_NOT_EMPTY")
    @Email(message = "EMAIL_INVALID")
    private String email;

    @NotBlank(message = "FIELD_IS_NOT_EMPTY")
    private String password;
}
