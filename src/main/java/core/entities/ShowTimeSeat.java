package core.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import core.enums.SeatStatus;
import core.enums.StatusReason;
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
    @JsonBackReference
    private ShowTime showTime;

    @ManyToOne
    @JoinColumn(name = "seatId")
    @JsonBackReference
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_status")
    private SeatStatus status = SeatStatus.AVAILABLE;

    @Column(name = "hold_expires_at")
    private ZonedDateTime holdExpiresAt = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "description")
    private StatusReason description = StatusReason.NONE;

    @Column(name = "is_modified")
    private boolean isModified = false;

    @Column(name = "user_holding")
    private UUID userHolding;

}
