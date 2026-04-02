package core.dto.request.showtimeseat;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ShowTimeSeatHoldRequest {

    @NotEmpty(message = "FIELD_IS_NOT_EMPTY")
    private List<UUID> showTimeSeatIds;

}
