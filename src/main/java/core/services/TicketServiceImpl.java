package core.services;


import core.dto.request.ticket.TicketCreateRequest;
import core.dto.request.ticket.TicketItemRequest;
import core.dto.request.ticket.TicketUpdateRequest;
import core.dto.response.PageResponse;
import core.dto.response.TicketResponse;
import core.entities.ShowTimeSeat;
import core.entities.Ticket;
import core.entities.User;
import core.enums.SeatStatus;
import core.enums.StatusReason;
import core.exceptions.AppException;
import core.exceptions.ErrorCode;
import core.factory.TicketPriceFactory;
import core.mapper.TicketMapper;
import core.repositories.ShowTimeSeatRepository;
import core.repositories.TicketRepository;
import core.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService{

    private final TicketRepository ticketRepository;

    private final TicketMapper ticketMapper;

    private final TicketPriceFactory ticketPriceFactory;

    private final UserRepository userRepository;

    private final ShowTimeSeatRepository showTimeSeatRepository;

    private final EmailReminderService emailReminderService;


    @Override
    @Transactional
    public List<TicketResponse> create(TicketCreateRequest request) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<UUID> showTimeSeatIds = request.getItems().stream()
                .map(TicketItemRequest::getShowTimeSeatId)
                .collect(Collectors.toList());

        List<ShowTimeSeat> showTimeSeats = showTimeSeatRepository.findAllById(showTimeSeatIds);

        if(showTimeSeats.size() != showTimeSeatIds.size()){
            throw new AppException(ErrorCode.SHOW_TIME_SEAT_NOT_EXISTED);
        }

        Map<UUID, ShowTimeSeat> seatMap = showTimeSeats.stream()
                .collect(Collectors.toMap(ShowTimeSeat::getShowTimeSeatId, s -> s));

        List<Ticket> tickets = new ArrayList<>();

        for (TicketItemRequest item : request.getItems()) {
            ShowTimeSeat seat = seatMap.get(item.getShowTimeSeatId());

            if (seat.getStatus() == SeatStatus.RESERVED) {
                throw new AppException(ErrorCode.SEAT_ALREADY_HELD_OR_BOOKED);
            }

            if(seat.getStatus() == SeatStatus.HOLDING && seat.getUserHolding().equals(UUID.fromString(userId))){
                BigDecimal basePrice = seat.getShowTime().getBasePrice();
                BigDecimal priceForThisTicket = ticketPriceFactory.getStrategy(item.getTicketType()).calculate(basePrice);

                Ticket ticket = new Ticket();
                ticket.setUser(user);
                ticket.setShowTimeSeat(seat);
                ticket.setTicketType(item.getTicketType());
                ticket.setPrice(priceForThisTicket);

                tickets.add(ticket);

                seat.setStatus(SeatStatus.RESERVED);
                seat.setHoldExpiresAt(null);
                seat.setDescription(StatusReason.WAITING_PAYMENT);
            }else {
                throw new AppException(ErrorCode.UNABLE_TO_PAY);
            }
        }

        ticketRepository.saveAll(tickets);
        showTimeSeatRepository.saveAll(showTimeSeats);

        return ticketMapper.toTicketResponse(tickets);
    }


    @Override
    public PageResponse<TicketResponse> findAllTicketByUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("bookingTime").descending());

        Page<Ticket> ticketPage = ticketRepository.findAllTicketByUser(userId, pageable);

        List<TicketResponse> ticketResponses = ticketPage.getContent().stream()
                .map(ticketMapper::toTicketResponse)
                .toList();

        return PageResponse.<TicketResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(ticketPage.getTotalPages())
                .totalElements(ticketPage.getTotalElements())
                .data(ticketResponses)
                .build();
    }

    @Override
    @Transactional
    public TicketResponse update(UUID id, TicketUpdateRequest request) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TICKET_NOT_EXISTED));

        ticketMapper.update(ticket, request);

        return ticketMapper.toTicketResponse(ticketRepository.save(ticket));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ticketRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.TICKET_NOT_EXISTED));
        ticketRepository.deleteById(id);
    }

    @Override
    @Transactional
    @Scheduled(cron = "${app.cron.send-email}")
    public void scanAndSendReminders() {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime start = nowUTC.plusMinutes(30);
        ZonedDateTime end = nowUTC.plusMinutes(60);


        List<Ticket> tickets = ticketRepository.findTicketsToRemind(start, end);

//        log.info("Danh sách vé:");
//        for (Ticket t : tickets){
//            log.info("Đã tìm thấy vé cần nhắc hẹn: ID = {}, Phim = {}, Email = {}",
//                    t.getTicketId(),
//                    t.getShowTimeSeat().getShowTime().getMovie().getTitle(),
//                    t.getUser().getEmail());
//        }


        for (Ticket ticket : tickets) {
            // 1. Chuẩn bị params
            Map<String, Object> params = new HashMap<>();
            params.put("movie_name", ticket.getShowTimeSeat().getShowTime().getMovie().getTitle());
            params.put("seat_number", ticket.getShowTimeSeat().getSeat().getSeatNumber());

            // 2. Gọi async service (Nó sẽ chạy ở thread khác)
            emailReminderService.sendBookingReminder(ticket.getUser().getEmail(), ticket.getUser().getUsername(), params);

            // 3. Đánh dấu đã xử lý ngay lập tức trong Transaction hiện tại
            ticket.setReminded(true);
            ticketRepository.save(ticket);
        }

    }



}












