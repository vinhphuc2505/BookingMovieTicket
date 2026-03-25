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
public class MovieCreateRequest {

    @NotBlank(message = "FIELD_IS_NOT_EMPTY")
    private String title;

    private int duration;

    @NotBlank(message = "FIELD_IS_NOT_EMPTY")
    private String genre;

    private int rating;

    private String description;

    private LocalDate releaseAt;
}
