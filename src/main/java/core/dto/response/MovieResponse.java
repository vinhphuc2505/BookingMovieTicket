package core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MovieResponse {
    private UUID movieId;

    private String title;

    private int duration;

    private String genre;

    private int rating;

    private String description;

    private LocalDate releaseAt;
}
