package core.service;

import core.dto.request.ticket.TicketCreateRequest;
import core.dto.request.ticket.TicketItemRequest;
import core.entities.ShowTime;
import core.entities.ShowTimeSeat;
import core.enums.SeatStatus;
import core.enums.TicketType;
import core.exceptions.AppException;
import core.exceptions.ErrorCode;
import core.repositories.ShowTimeRepository;
import core.repositories.ShowTimeSeatRepository;
import core.services.EmailReminderService;
import core.services.EmailReminderServiceImpl;
import core.services.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private ShowTimeSeatRepository showTimeSeatRepository;

    @MockitoBean
    private EmailReminderService emailReminderService;

    private final UUID AVAILABLE_SEAT_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private final UUID SHOWTIME_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final String USER_ID = "00000000-0000-0000-0000-000000000001";

    @Test
    @Sql(scripts = "/data/setup_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testCreateTicket_SoldOut_ShouldFail() {
        // 1. Setup: Giả lập User hiện tại
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList()));

        // 2. Chỉnh sửa ShowTime: Ép availableSeat về 0 để test Sold Out
        ShowTime showTime = showTimeRepository.findById(SHOWTIME_ID).orElseThrow();
        showTime.setAvailableSeat(0);
        showTimeRepository.saveAndFlush(showTime);

        // 3. Giả lập ghế A1 đang được giữ bởi chính User này (Yêu cầu của logic create)
        ShowTimeSeat seat = showTimeSeatRepository.findById(AVAILABLE_SEAT_ID).orElseThrow();
        seat.setStatus(SeatStatus.HOLDING);
        seat.setUserHolding(UUID.fromString(USER_ID));
        showTimeSeatRepository.saveAndFlush(seat);

        // 4. Thực thi: Thử tạo vé khi đã sold out
        TicketCreateRequest request = new TicketCreateRequest();
        TicketItemRequest item = new TicketItemRequest();
        item.setShowTimeSeatId(AVAILABLE_SEAT_ID);
        item.setTicketType(TicketType.ADULT);
        request.setItems(List.of(item));

        // 5. Kiểm chứng
        AppException exception = assertThrows(AppException.class, () -> ticketService.create(request));
        assertEquals(ErrorCode.TICKET_SOLD_OUT, exception.getErrorCode());

        log.info("Test Sold Out thành công!");
    }

    @Test
    @Sql(scripts = "/data/setup_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testCreateTicket_RaceCondition_LockingShowTime() throws InterruptedException {
        // Giả lập 2 ghế khác nhau nhưng cùng 1 ShowTime
        // Thread A mua ghế 1, Thread B mua ghế 2
        // Cả 2 đều phải Lock dòng ShowTime để trừ availableSeat

        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numberOfThreads);

        // Chuẩn bị 2 ghế đã HOLDING cho User
        prepareSeat(AVAILABLE_SEAT_ID, USER_ID);
        UUID seatId2 = UUID.fromString("66666666-6666-6666-6666-666666666666"); // Ghế A2
        prepareSeat(seatId2, USER_ID);

        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            final UUID targetSeatId = (i == 0) ? AVAILABLE_SEAT_ID : seatId2;
            executorService.submit(() -> {
                try {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList()));

                    startLatch.await();

                    TicketCreateRequest request = new TicketCreateRequest();
                    TicketItemRequest item = new TicketItemRequest();
                    item.setShowTimeSeatId(targetSeatId);
                    item.setTicketType(TicketType.ADULT);
                    request.setItems(List.of(item));

                    ticketService.create(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("Error: {}", e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await(10, TimeUnit.SECONDS);

        // Kiểm tra availableSeat sau khi 2 thread thành công
        ShowTime finalShowTime = showTimeRepository.findById(SHOWTIME_ID).get();
        // Giả sử ban đầu trong setup_data.sql có 100 ghế
        log.info("Available seats left: {}", finalShowTime.getAvailableSeat());
        assertEquals(2, successCount.get());

        executorService.shutdown();
    }

    private void prepareSeat(UUID id, String userId) {
        ShowTimeSeat seat = showTimeSeatRepository.findById(id).orElseThrow();
        seat.setStatus(SeatStatus.HOLDING);
        seat.setUserHolding(UUID.fromString(userId));
        showTimeSeatRepository.saveAndFlush(seat);
    }
}
