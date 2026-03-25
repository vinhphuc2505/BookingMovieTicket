package core.entities;


import core.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "showTimeSeat")
public class ShowTimeSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "show_time_seat_id")
    private UUID showTimeSeatId;

    @ManyToOne
    @JoinColumn(name = "showTimeId")
    private ShowTime showTime;

    @ManyToOne
    @JoinColumn(name = "seatId")
    private Seat seat;

    @OneToOne
    @JoinColumn(name = "ticketId")
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "seat_status")
    private SeatStatus status;

    @Column(name = "hold_expires_at")
    private ZonedDateTime holdExpiresAt;

}
