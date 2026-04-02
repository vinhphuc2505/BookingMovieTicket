package core.dto.response;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import core.enums.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowTimeSeatResponse {
    private UUID showTimeSeatId;

    private ShowTimeResponse showTime;

    @JsonIgnoreProperties("room")
    private SeatResponse seat;

    private SeatStatus status;

    private ZonedDateTime holdExpiresAt;
}
