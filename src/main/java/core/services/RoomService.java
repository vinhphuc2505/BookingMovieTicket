package core.services;

import core.dto.request.room.RoomCreateRequest;
import core.dto.request.room.RoomUpdateRequest;
import core.dto.response.PageResponse;
import core.dto.response.RoomResponse;

import java.util.UUID;

public interface RoomService {
    RoomResponse create(RoomCreateRequest request);

    PageResponse<RoomResponse> getRoom(int page, int size);

    RoomResponse update(UUID id, RoomUpdateRequest request);

    void delete(UUID id);
}