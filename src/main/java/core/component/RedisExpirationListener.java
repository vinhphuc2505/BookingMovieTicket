package core.component;


import core.enums.SeatStatus;
import core.enums.StatusReason;
import core.repositories.ShowTimeSeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RedisExpirationListener extends KeyExpirationEventMessageListener {
    private final ShowTimeSeatRepository showTimeSeatRepository;

    public RedisExpirationListener(RedisMessageListenerContainer listenerContainer,
                                   ShowTimeSeatRepository showTimeSeatRepository) {
        super(listenerContainer);
        this.showTimeSeatRepository = showTimeSeatRepository;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();

        if (expiredKey.startsWith("seat:hold:")) {
            String seatId = expiredKey.replace("seat:hold:", "");
            handleSeatExpiration(UUID.fromString(seatId));
        }
    }

    private void handleSeatExpiration(UUID seatId) {
        log.info("Redis Worker: Seat {} expired. Checking database...", seatId);

        // chuyển trạng thái về AVAILABLE
        showTimeSeatRepository.findById(seatId).ifPresent(seat -> {
            if (seat.getStatus() == SeatStatus.HOLDING) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setDescription(StatusReason.TIMEOUT_RELEASED);
                seat.setHoldExpiresAt(null);
                seat.setUserHolding(null);
                showTimeSeatRepository.save(seat);
                log.info("Redis Worker: Released seat {}", seatId);
            }
        });
    }
}
