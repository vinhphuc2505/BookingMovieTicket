package core.dto.request.movie;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MovieUpdateRequest {

    @NotBlank(message = "FIELD_IS_NOT_EMPTY")
    private String title;

    @NotBlank(message = "FIELD_IS_NOT_EMPTY")
    private int duration;

    @NotBlank(message = "FIELD_IS_NOT_EMPTY")
    private String genre;

    @NotBlank(message = "FIELD_IS_NOT_EMPTY")
    private String description;

    @NotBlank(message = "FIELD_IS_NOT_EMPTY")
    private LocalDate releaseAt;
}
