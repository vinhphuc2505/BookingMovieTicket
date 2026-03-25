package core.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatResponse {
    private UUID seatId;

    @JsonIgnoreProperties("seats")
    private RoomResponse room;

    private String seatNumber;
}
