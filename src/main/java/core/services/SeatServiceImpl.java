package core.services;


import core.dto.request.seat.SeatCreateRequest;
import core.dto.response.PageResponse;
import core.dto.response.SeatResponse;
import core.entities.Room;
import core.entities.Seat;
import core.exceptions.AppException;
import core.exceptions.ErrorCode;
import core.mapper.SeatMapper;
import core.repositories.RoomRepository;
import core.repositories.SeatRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService{
    private final SeatRepository seatRepository;

    private final SeatMapper seatMapper;

    private final RoomRepository roomRepository;


    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public List<SeatResponse> create(SeatCreateRequest request) {

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));

        List<String> seatNumbers = request.getSeatNumbers();

        boolean exists = seatRepository.existsByRoomAndSeatNumberIn(room, seatNumbers);

        if(exists){
            throw new AppException(ErrorCode.SEAT_EXISTED);
        }

        List<Seat> seats = request.getSeatNumbers().stream()
                .map(seatNumber -> {
                    Seat seat = new Seat();
                    seat.setRoom(room);
                    seat.setSeatNumber(seatNumber);
                    return seat;
                })
                .collect(Collectors.toList());

        seatRepository.saveAll(seats);

        return seatMapper.toSeatResponse(seats);
    }

    @Override
    public PageResponse<SeatResponse> getSeatByRoom(UUID roomId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("seatNumber").descending());

        Page<Seat> seatPage = seatRepository.findAllByRoomId(roomId, pageable);

        List<SeatResponse> seatResponses = seatPage.getContent().stream()
                .map(seatMapper::toSeatResponse)
                .toList();

        return PageResponse.<SeatResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(seatPage.getTotalPages())
                .totalElements(seatPage.getTotalElements())
                .data(seatResponses)
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(UUID id) {
        seatRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_EXISTED));
        seatRepository.deleteById(id);
    }
}





