package core.services;


import core.dto.request.ticket.TicketCreateRequest;
import core.dto.request.ticket.TicketItemRequest;
import core.dto.request.ticket.TicketUpdateRequest;
import core.dto.response.PageResponse;
import core.dto.response.TicketResponse;
import core.entities.*;
import core.enums.SeatStatus;
import core.enums.StatusReason;
import core.exceptions.AppException;
import core.exceptions.ErrorCode;
import core.factory.TicketPriceFactory;
import core.mapper.TicketMapper;
import core.repositories.*;
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
import java.time.format.DateTimeFormatter;
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

    private final ShowTimeRepository showTimeRepository;


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
        UUID showTimeId = showTimeSeats.get(0).getShowTime().getShowTimeId();

        ShowTime showTime = showTimeRepository.findByIdWithLock(showTimeId)
                .orElseThrow(() -> new AppException(ErrorCode.SHOW_TIME_NOT_EXISTED));

        int availableSeat = showTime.getAvailableSeat() - tickets.size();

        if(availableSeat < 0){
            throw new AppException(ErrorCode.TICKET_SOLD_OUT);
        }

        showTime.setAvailableSeat(availableSeat);

        ticketRepository.saveAll(tickets);
        showTimeSeatRepository.saveAll(showTimeSeats);
        showTimeRepository.save(showTime);

        return ticketMapper.toTicketResponse(tickets);
    }


    @Override
    public PageResponse<TicketResponse> findAllTicketByUser(int page, int size) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("bookingTime").descending());

        Page<Ticket> ticketPage = ticketRepository.findAllTicketByUser(UUID.fromString(userId), pageable);

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")
                .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));


        List<Ticket> tickets = ticketRepository.findTicketsToRemind(start, end);

        List<Ticket> ticketListUpdate = new ArrayList<>();

//        log.info("Danh sách vé:");
//        for (Ticket t : tickets){
//            log.info("Đã tìm thấy vé cần nhắc hẹn: ID = {}, Phim = {}, Email = {}",
//                    t.getTicketId(),
//                    t.getShowTimeSeat().getShowTime().getMovie().getTitle(),
//                    t.getUser().getEmail());
//        }

        if(!tickets.isEmpty()){
            Map<UUID, List<Ticket>> userTicketsMap = tickets.stream()
                    .collect(Collectors.groupingBy(t -> t.getUser().getUserId()));

            userTicketsMap.forEach((userId, userTickets) -> {
                User user = userTickets.get(0).getUser();
                Ticket ticket = userTickets.get(0);
                String movie = ticket.getShowTimeSeat().getShowTime().getMovie().getTitle();
                String roomName = ticket.getShowTimeSeat().getShowTime().getRoom().getRoomName();
                String startTime = formatter.format(ticket.getShowTimeSeat().getShowTime().getStartTime());
                int totalSeat = userTickets.size();
//                List<String> seatNumbers = userTickets.stream()
//                        .map(t -> t.getShowTimeSeat().getSeat().getSeatNumber())
//                        .toList();
                String seatNumbers = userTickets.stream()
                        .map(t -> t.getShowTimeSeat().getSeat().getSeatNumber())
                        .collect(Collectors.joining(", "));

                Map<String, Object> params = new HashMap<>();
                params.put("customer_name", user.getUsername());
                params.put("movie_name", movie);
                params.put("room_name", roomName);
                params.put("start_time", startTime);
                params.put("total_seat", totalSeat);
                params.put("seat_numbers", seatNumbers);

                // Gọi async service (Nó sẽ chạy ở thread khác)
                emailReminderService.sendBookingReminder(ticket.getUser().getEmail(), ticket.getUser().getUsername(), params);

                userTickets.forEach(t -> t.setReminded(true));

                log.info("Đã gửi 1 mail tổng hợp cho {}, gồm {} vé: ghế {}",
                        user.getEmail(), userTickets.size(), seatNumbers);

                ticketListUpdate.addAll(userTickets);

            });
        }
        ticketRepository.saveAll(ticketListUpdate);
    }



}












