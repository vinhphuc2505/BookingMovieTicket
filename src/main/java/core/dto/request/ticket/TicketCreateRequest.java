package core.dto.request.ticket;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TicketCreateRequest {

    @NotEmpty(message = "FIELD_IS_NOT_EMPTY")
    private List<TicketItemRequest> items;

}
