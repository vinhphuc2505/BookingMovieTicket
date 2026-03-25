package core.dto.response;



import com.fasterxml.jackson.annotation.JsonInclude;
import core.entities.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomResponse {
    private UUID roomId;

    private String roomName;

    private int totalSeats;

    private List<Seat> seats;
}
