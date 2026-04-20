package core.dto.request.user;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    @NotBlank(message = "FIELD_IS_NOT_EMPTY")
    private String oldPassword;

    @NotBlank(message = "FIELD_IS_NOT_EMPTY")
    private String password;

    @NotBlank(message = "FIELD_IS_NOT_EMPTY")
    private String passwordAgain;
}
