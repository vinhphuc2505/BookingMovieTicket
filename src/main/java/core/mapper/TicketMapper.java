package core.mapper;


import core.dto.request.ticket.TicketUpdateRequest;
import core.dto.response.TicketResponse;
import core.entities.ShowTimeSeat;
import core.entities.Ticket;
import core.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {User.class, ShowTimeSeat.class})
public interface TicketMapper {
    TicketResponse toTicketResponse(Ticket ticket);

    List<TicketResponse> toTicketResponse(List<Ticket> ticketList);

    void update(@MappingTarget Ticket ticket, TicketUpdateRequest request);
}
