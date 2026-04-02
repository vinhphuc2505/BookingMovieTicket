package core.services;

import core.dto.request.ticket.TicketCreateRequest;
import core.dto.request.ticket.TicketUpdateRequest;
import core.dto.response.PageResponse;
import core.dto.response.TicketResponse;

import java.util.List;
import java.util.UUID;

public interface TicketService {
    List<TicketResponse> create(TicketCreateRequest request);

    PageResponse<TicketResponse> findAllTicketByUser(UUID userId, int page, int size);

    TicketResponse update(UUID id,TicketUpdateRequest request);

    void delete(UUID id);

    void scanAndSendReminders();
}
