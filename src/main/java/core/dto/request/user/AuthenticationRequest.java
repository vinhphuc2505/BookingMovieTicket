package core.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @NotBlank(message = "FIELD_IS_NOT_EMPTY")
    private String loginName;

    @NotBlank(message = "FIELD_IS_NOT_EMPTY")
    private String password;
}