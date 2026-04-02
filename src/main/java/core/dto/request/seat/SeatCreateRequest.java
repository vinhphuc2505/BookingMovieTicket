package core.dto.request.seat;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatCreateRequest {
    @NotNull(message = "FIELD_IS_NOT_EMPTY")
    private UUID roomId;

    @NotEmpty(message = "FIELD_IS_NOT_EMPTY")
    private List<String> seatNumbers;
}
