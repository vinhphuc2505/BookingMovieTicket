package core.services;


import core.dto.request.showtimeseat.ShowTimeSeatCreateRequest;
import core.dto.request.showtimeseat.ShowTimeSeatHoldRequest;
import core.dto.response.PageResponse;
import core.dto.response.ShowTimeResponse;
import core.dto.response.ShowTimeSeatResponse;
import core.entities.Seat;
import core.entities.ShowTime;
import core.entities.ShowTimeSeat;
import core.enums.SeatStatus;
import core.enums.StatusReason;
import core.exceptions.AppException;
import core.exceptions.ErrorCode;
import core.mapper.ShowTimeSeatMapper;
import core.repositories.SeatRepository;
import core.repositories.ShowTimeRepository;
import core.repositories.ShowTimeSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowTimeSeatServiceImpl implements ShowTimeSeatService{
    private final ShowTimeSeatMapper showTimeSeatMapper;

    private final ShowTimeSeatRepository showTimeSeatRepository;

    private final ShowTimeRepository showTimeRepository;

    private final SeatRepository seatRepository;

    private final StringRedisTemplate stringRedisTemplate;

    @Lazy
    @Autowired
    private ShowTimeSeatServiceImpl self;


    @Override
    @Transactional
    public List<ShowTimeSeatResponse> create(ShowTimeSeatCreateRequest request) {

        ShowTime showTime = showTimeRepository.findById(request.getShowTimeId())
                .orElseThrow(() -> new AppException(ErrorCode.SHOW_TIME_NOT_EXISTED));

        List<Seat> seats = seatRepository.findAllById(request.getSeatIds());

        if (seats.size() != request.getSeatIds().size()) {
            throw new AppException(ErrorCode.SEAT_NOT_EXISTED);
        }

        for(Seat seat : seats){
            if(showTimeSeatRepository.existsBySeat(seat) && showTimeSeatRepository.existsByShowTime(showTime)){
                throw new AppException(ErrorCode.SHOW_TIME_SEAT_EXISTED);
            }
        }

        List<ShowTimeSeat> showTimeSeats = seats.stream()
                .map(seat -> {
                    ShowTimeSeat showTimeSeat = new ShowTimeSeat();
                    showTimeSeat.setShowTime(showTime);
                    showTimeSeat.setSeat(seat);
                    return showTimeSeat;
                })
                .collect(Collectors.toList());

        showTimeSeatRepository.saveAll(showTimeSeats);

        return showTimeSeatMapper.toShowTimeSeatResponse(showTimeSeats);
    }


    @Override
    public PageResponse<ShowTimeSeatResponse> findAllByShowTime(UUID id, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("seat").descending());

        Page<ShowTimeSeat> showTimeSeatPage = showTimeSeatRepository.findAllByShowTime(id, pageable);

        List<ShowTimeSeatResponse> showTimeSeatResponses = showTimeSeatPage.getContent().stream()
                .map(showTimeSeatMapper::toShowTimeSeatResponse)
                .toList();

        return PageResponse.<ShowTimeSeatResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(showTimeSeatPage.getTotalPages())
                .totalElements(showTimeSeatPage.getTotalElements())
                .data(showTimeSeatResponses)
                .build();
    }


    @Override
    @Transactional
    public List<ShowTimeSeatResponse> holdingSeat(ShowTimeSeatHoldRequest request) {
        var id = SecurityContextHolder.getContext().getAuthentication().getName();

        //Sắp xếp id tránh deadlock
        List<UUID> sortedIds = request.getShowTimeSeatIds().stream()
                .distinct()
                .sorted()
                .toList();

        List<ShowTimeSeat> showTimeSeats = showTimeSeatRepository.findAllByIdAndLockSeat(sortedIds);

        if (showTimeSeats.size() != sortedIds.size()) {
            throw new AppException(ErrorCode.SHOW_TIME_SEAT_NOT_EXISTED);
        }

        boolean anyNotAvailable = showTimeSeats.stream()
                .anyMatch(s -> s.getStatus() != SeatStatus.AVAILABLE);

        if (anyNotAvailable) {
            throw new AppException(ErrorCode.SEAT_ALREADY_HELD_OR_BOOKED);
        }

        ZonedDateTime expireAt = ZonedDateTime.now().plusMinutes(5);

        showTimeSeats.forEach(seat -> {
            seat.setStatus(SeatStatus.HOLDING);
            seat.setDescription(StatusReason.CUSTOMER_HOLDING);
            seat.setHoldExpiresAt(expireAt);
            seat.setModified(true);
            seat.setUserHolding(UUID.fromString(id));
        });

        showTimeSeatRepository.saveAll(showTimeSeats);

        try{
            for (ShowTimeSeat seat : showTimeSeats) {
                String key = "seat:hold:" + seat.getShowTimeSeatId();
                stringRedisTemplate.opsForValue().set(key, "holding", 5, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.error("Failed to set Redis TTL for seat, fallback to Cron Job", e);
        }

        return showTimeSeatMapper.toShowTimeSeatResponse(showTimeSeats);
    }

    @Override
    public List<ShowTimeSeatResponse> holdingSeatV2(ShowTimeSeatHoldRequest request) {
        var id = SecurityContextHolder.getContext().getAuthentication().getName();

        //Sắp xếp id tránh deadlock
        List<UUID> sortedIds = request.getShowTimeSeatIds().stream()
                .distinct()
                .sorted()
                .toList();

        List<ShowTimeSeat> showTimeSeats = new ArrayList<>();

        ZonedDateTime expireAt = ZonedDateTime.now().plusMinutes(5);

        try{
            for (UUID showTimeSeatId : sortedIds){
                ShowTimeSeat showTimeSeat = self.holdingSingleSeat(UUID.fromString(id), showTimeSeatId, expireAt);
                showTimeSeats.add(showTimeSeat);
            }
        }catch (Exception e){
            log.error("Error hodling seat: {}", e.getMessage());
            for (ShowTimeSeat showTimeSeat : showTimeSeats){
                try {
                    self.releaseRollback(showTimeSeat.getShowTimeSeatId());
                } catch (Exception rollbackEx) {
                    log.error("Rollback fail {}: {}", showTimeSeat.getShowTimeSeatId(), rollbackEx.getMessage());
                }
            }
            throw e;
        }

        try{
            for (ShowTimeSeat seat : showTimeSeats) {
                String key = "seat:hold:" + seat.getShowTimeSeatId();
                stringRedisTemplate.opsForValue().set(key, "holding", 5, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.error("Failed to set Redis TTL for seat, fallback to Cron Job", e);
        }

        return showTimeSeatMapper.toShowTimeSeatResponse(showTimeSeats);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        showTimeSeatRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SHOW_TIME_SEAT_NOT_EXISTED));
        showTimeRepository.deleteById(id);
    }

    @Override
    @Transactional
    @Scheduled(cron = "${app.cron.cleanup-seats}")
    public void handleSeatCleanup() {
        ZonedDateTime now = ZonedDateTime.now();

        int releaseUpdate = showTimeSeatRepository.releaseExpiredHoldingSeats(now);

        if(releaseUpdate > 0){
            log.info("Released {} expired holding seats", releaseUpdate);
        }

//        ZonedDateTime cleanupCutoff = now.minusMinutes(15);

//        int releaseDelete = showTimeSeatRepository.deleteObsoleteSeats(cleanupCutoff);
//
//        if(releaseDelete > 0){
//            log.info("Deleted {} obsolete seats from past showtimes", releaseDelete);
//        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ShowTimeSeat holdingSingleSeat(UUID userId, UUID showTimeSeatId, ZonedDateTime expireAt){
        ShowTimeSeat showTimeSeat = showTimeSeatRepository.findAndUpdate(showTimeSeatId);

        if(showTimeSeat == null){
            throw new AppException(ErrorCode.SHOW_TIME_SEAT_NOT_EXISTED);
        }

        if (showTimeSeat.getStatus() != SeatStatus.AVAILABLE) {
            throw new AppException(ErrorCode.SEAT_ALREADY_HELD_OR_BOOKED);
        }

        showTimeSeat.setStatus(SeatStatus.HOLDING);
        showTimeSeat.setHoldExpiresAt(expireAt);
        showTimeSeat.setDescription(StatusReason.CUSTOMER_HOLDING);
        showTimeSeat.setUserHolding(userId);

        showTimeSeatRepository.save(showTimeSeat);

        return showTimeSeat;

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void releaseRollback(UUID showTimeSeatId){
        ShowTimeSeat showTimeSeat = showTimeSeatRepository.findAndUpdate(showTimeSeatId);

        if(showTimeSeat == null){
            throw new AppException(ErrorCode.SHOW_TIME_SEAT_NOT_EXISTED);
        }

        showTimeSeat.setStatus(SeatStatus.AVAILABLE);
        showTimeSeat.setHoldExpiresAt(null);
        showTimeSeat.setDescription(StatusReason.ROLLBACK);
        showTimeSeat.setUserHolding(null);

        showTimeSeatRepository.save(showTimeSeat);
    }


}






