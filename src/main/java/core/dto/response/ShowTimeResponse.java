package core.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class ShowTimeResponse {
    private UUID showTimeId;

    @JsonIgnoreProperties("seats")
    private RoomResponse room;

    private MovieResponse movie;

    private ZonedDateTime startTime;

    private ZonedDateTime endTime;

    private BigDecimal basePrice;

//    private int availableSeat;
}
