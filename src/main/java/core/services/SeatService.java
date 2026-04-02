package core.services;


import core.dto.request.seat.SeatCreateRequest;
import core.dto.response.PageResponse;
import core.dto.response.SeatResponse;

import java.util.List;
import java.util.UUID;

public interface SeatService {
    List<SeatResponse> create(SeatCreateRequest request);

    PageResponse<SeatResponse> getSeatByRoom(UUID roomId, int page, int size);

    void delete(UUID id);
}
