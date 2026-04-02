package core.mapper;


import core.dto.request.showtimeseat.ShowTimeSeatCreateRequest;
import core.dto.request.showtimeseat.ShowTimeSeatHoldRequest;
import core.dto.response.ShowTimeSeatResponse;
import core.entities.Seat;
import core.entities.ShowTime;
import core.entities.ShowTimeSeat;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ShowTime.class, Seat.class})
public interface ShowTimeSeatMapper {
    ShowTimeSeat toShowTimeSeat(ShowTimeSeatCreateRequest request);

    ShowTimeSeatResponse toShowTimeSeatResponse(ShowTimeSeat showTimeSeat);

    List<ShowTimeSeatResponse> toShowTimeSeatResponse(List<ShowTimeSeat> showTimeSeats);

    void holdingSeat(@MappingTarget ShowTimeSeat showTimeSeat, ShowTimeSeatHoldRequest request);
}
