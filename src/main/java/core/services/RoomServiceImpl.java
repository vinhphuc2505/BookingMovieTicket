package core.services;


import core.dto.request.room.RoomCreateRequest;
import core.dto.request.room.RoomUpdateRequest;
import core.dto.response.PageResponse;
import core.dto.response.RoomResponse;
import core.entities.Room;
import core.exceptions.AppException;
import core.exceptions.ErrorCode;
import core.mapper.RoomMapper;
import core.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;

    private final RoomMapper roomMapper;


    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public RoomResponse create(RoomCreateRequest request) {
        Room room = roomMapper.toRoom(request);

        return roomMapper.toRoomResponse(roomRepository.save(room));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<RoomResponse> getRoom(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("roomName").descending());

        Page<Room> roomPage = roomRepository.findAll(pageable);

        List<RoomResponse> roomResponses = roomPage.getContent().stream()
                .map(roomMapper::toRoomResponse)
                .toList();


        return PageResponse.<RoomResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(roomPage.getTotalPages())
                .totalElements(roomPage.getTotalElements())
                .data(roomResponses)
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public RoomResponse update(UUID id, RoomUpdateRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));
        roomMapper.update(room, request);
        return roomMapper.toRoomResponse(roomRepository.save(room));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(UUID id) {
        roomRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));
        roomRepository.deleteById(id);
    }
}
