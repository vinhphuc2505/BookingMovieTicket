package core.entities;


import core.enums.PaymentStatus;
import core.enums.TicketType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ticket_id")
    private UUID ticketId;

    @OneToOne
    @JoinColumn(name = "showTimeSeat_id", unique = true)
    private ShowTimeSeat showTimeSeat;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_type")
    private TicketType ticketType = TicketType.ADULT;

    @Column(name = "price")
    private BigDecimal price;

    @CreationTimestamp
    @Column(name = "booking_time")
    private ZonedDateTime bookingTime = ZonedDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "is_reminded")
    private boolean isReminded = false;

}
