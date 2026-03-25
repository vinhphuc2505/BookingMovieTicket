package core.services;


import core.dto.request.seat.SeatCreateRequest;
import core.dto.response.SeatResponse;
import core.entities.Room;
import core.entities.Seat;
import core.exceptions.AppException;
import core.exceptions.ErrorCode;
import core.mapper.SeatMapper;
import core.repositories.RoomRepository;
import core.repositories.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService{
    private final SeatRepository seatRepository;

    private final SeatMapper seatMapper;

    private final RoomRepository roomRepository;


    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public SeatResponse create(SeatCreateRequest request) {
        Seat seat = seatMapper.toSeat(request);

        if(seatRepository.existsBySeatNumber(request.getSeatNumber())){
            throw new AppException(ErrorCode.SEAT_EXISTED);
        }

        Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));
        seat.setRoom(room);

        return seatMapper.toSeatResponse(seatRepository.save(seat));
    }

    @Override
    public List<SeatResponse> getSeatByRoom(UUID roomId) {
        return seatMapper.toSeatResponse(seatRepository.findAllByRoomId(roomId));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(UUID id) {
        seatRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_EXISTED));
        seatRepository.deleteById(id);
    }
}





