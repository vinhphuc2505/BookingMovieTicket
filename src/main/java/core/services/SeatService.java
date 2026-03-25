package core.services;


import core.dto.request.seat.SeatCreateRequest;
import core.dto.response.SeatResponse;

import java.util.List;
import java.util.UUID;

public interface SeatService {
    SeatResponse create(SeatCreateRequest request);

    List<SeatResponse> getSeatByRoom(UUID roomId);

    void delete(UUID id);
}
