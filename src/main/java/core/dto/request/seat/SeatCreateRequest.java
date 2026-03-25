package core.dto.request.seat;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatCreateRequest {
    private UUID roomId;

    private String seatNumber;
}
