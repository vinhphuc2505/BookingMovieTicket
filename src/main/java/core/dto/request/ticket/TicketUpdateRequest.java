package core.dto.request.ticket;


import core.enums.TicketType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TicketUpdateRequest {
    @NotNull(message = "FIELD_IS_NOT_EMPTY")
    private UUID showTimeSeatId;

    @NotNull(message = "FIELD_IS_NOT_EMPTY")
    @Valid
    private TicketType ticketType;

}
