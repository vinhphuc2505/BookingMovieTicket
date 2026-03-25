package core.dto.request.showtime;

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

    private UUID roomId;

    private UUID movieId;

    private ZonedDateTime startTime;

    private BigDecimal basePrice;
}
