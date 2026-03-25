package core.mapper;


import core.dto.request.room.RoomCreateRequest;
import core.dto.request.room.RoomUpdateRequest;
import core.dto.response.RoomResponse;
import core.entities.Room;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    Room toRoom(RoomCreateRequest request);

    RoomResponse toRoomResponse(Room room);

    List<RoomResponse> toRoomResponse(List<Room> rooms);

    void update(@MappingTarget Room room, RoomUpdateRequest request);
}
