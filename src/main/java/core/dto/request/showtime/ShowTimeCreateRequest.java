package core.dto.request.showtime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowTimeCreateRequest {
    @NotNull(message = "FIELD_IS_NOT_EMPTY")
    private UUID roomId;

    @NotNull(message = "FIELD_IS_NOT_EMPTY")
    private UUID movieId;

    @NotNull(message = "FIELD_IS_NOT_EMPTY")
    private ZonedDateTime startTime;

    @NotNull(message = "FIELD_IS_NOT_EMPTY")
    private BigDecimal basePrice;
}
