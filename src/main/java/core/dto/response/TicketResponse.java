package core.dto.response;

import core.enums.PaymentStatus;
import core.enums.TicketType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private UUID ticketId;

    private ShowTimeSeatResponse showTimeSeat;

    private UserResponse user;

    private TicketType ticketType;

    private BigDecimal price;

    private ZonedDateTime bookingTime;

    private PaymentStatus paymentStatus;
}
