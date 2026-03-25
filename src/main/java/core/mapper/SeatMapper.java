package core.mapper;


import core.dto.request.seat.SeatCreateRequest;
import core.dto.response.SeatResponse;
import core.entities.Seat;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoomMapper.class})
public interface SeatMapper {
    Seat toSeat(SeatCreateRequest request);

    SeatResponse toSeatResponse(Seat seat);

    List<SeatResponse> toSeatResponse(List<Seat> seats);
}
