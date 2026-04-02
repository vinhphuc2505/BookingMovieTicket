package core.dto.request.room;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomUpdateRequest {

    @NotBlank(message = "FIELD_IS_NOT_EMPTY")
    private String roomName;

    private int totalSeats;
}
