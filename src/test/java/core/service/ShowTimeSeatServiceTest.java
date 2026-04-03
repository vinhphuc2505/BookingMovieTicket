package core.service;



import core.controllers.TicketController;
import core.dto.request.showtimeseat.ShowTimeSeatHoldRequest;
import core.enums.SeatStatus;
import core.repositories.*;
import core.services.EmailReminderService;
import core.services.ShowTimeSeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
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

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class ShowTimeSeatServiceTest {

    @MockitoBean
    private TicketController ticketController;

    @MockitoBean
    private EmailReminderService emailReminderService;

    private final ShowTimeSeatService showTimeSeatService;

    private final ShowTimeSeatRepository showTimeSeatRepository;

    private final ShowTimeRepository showTimeRepository;

    private final SeatRepository seatRepository;

    private final RoomRepository roomRepository;

    private final MovieRepository movieRepository;

    private final UUID AVAILABLE_SEAT_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");

    private final String USER_ID = "00000000-0000-0000-0000-000000000001";


    @Test
    @Sql(scripts = "/data/setup_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testHoldingSeatV2_RaceCondition_ShouldAllowOnlyOneSuccess() throws InterruptedException {
        // 1. Giả lập Context bảo mật cho luồng chính (nếu cần)
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList()));
        SecurityContextHolder.setContext(securityContext);

        int numberOfThreads = 2; // Giả lập 2 người cùng nhấn nút "Đặt ghế"
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // Latch này dùng để các thread đợi nhau, cùng xuất phát một lúc
        CountDownLatch startLatch = new CountDownLatch(1);
        // Latch này dùng để luồng chính đợi các luồng con chạy xong hết mới kiểm tra kết quả
        CountDownLatch endLatch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Chuẩn bị Request đặt cùng 1 ghế AVAILABLE_SEAT_ID
        ShowTimeSeatHoldRequest request = new ShowTimeSeatHoldRequest();
        request.setShowTimeSeatIds(List.of(AVAILABLE_SEAT_ID));

        // 2. Chạy đa luồng
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // Đưa Authentication vào từng Thread con (vì SecurityContext không tự share giữa các luồng)
                    SecurityContextHolder.setContext(securityContext);

                    startLatch.await(); // Tất cả thread dừng ở đây đợi lệnh "bắn"

                    showTimeSeatService.holdingSeatV2(request);
                    successCount.incrementAndGet();
                    log.info("Thread {} thành công!", Thread.currentThread().getName());
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.error("Thread {} thất bại: {}", Thread.currentThread().getName(), e.getMessage());
                } finally {
                    endLatch.countDown();
                    SecurityContextHolder.clearContext();
                }
            });
        }

        // 3. Kích hoạt Race Condition
        long startTime = System.currentTimeMillis();
        startLatch.countDown(); // bắt đầu race

        // Đợi tối đa 10s để các thread xử lý xong
        boolean completed = endLatch.await(10, TimeUnit.SECONDS);
        assertTrue(completed, "Các thread không hoàn thành đúng hạn");

        // 4. Kiểm chứng (Assertions)
        log.info("Kết quả sau race condition: Success={}, Failure={}", successCount.get(), failureCount.get());

        // Nếu @Lock hoạt động đúng:
        // - Chỉ 1 người được thành công (Success = 1)
        // - Người kia phải thất bại vì trạng thái ghế đã chuyển sang HOLDING (Failure = 1)
        assertEquals(1, successCount.get(), "Chỉ có thể có 1 giao dịch thành công");
        assertEquals(1, failureCount.get(), "Giao dịch thứ hai phải thất bại do Lock");

        // Kiểm tra DB lần cuối xem ghế có thực sự thuộc về user đó không
        var finalSeat = showTimeSeatRepository.findById(AVAILABLE_SEAT_ID).orElseThrow();
        assertEquals(SeatStatus.HOLDING, finalSeat.getStatus());
        assertEquals(UUID.fromString(USER_ID), finalSeat.getUserHolding());

        executorService.shutdown();
    }


    @AfterEach
    void cleanUp() {
        showTimeSeatRepository.deleteAllInBatch();
        showTimeRepository.deleteAllInBatch();
        seatRepository.deleteAllInBatch();
        roomRepository.deleteAllInBatch();
        movieRepository.deleteAllInBatch();
    }
}










