package core.services;

import core.dto.request.room.RoomCreateRequest;
import core.dto.request.room.RoomUpdateRequest;
import core.dto.response.RoomResponse;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    RoomResponse create(RoomCreateRequest request);

    List<RoomResponse> getRoom();

    RoomResponse update(UUID id, RoomUpdateRequest request);

    void delete(UUID id);
}