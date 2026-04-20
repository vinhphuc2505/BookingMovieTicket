package core.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "showTime")
public class ShowTime {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "show_time_id")
    private UUID showTimeId;

    @ManyToOne
    @JoinColumn(name = "roomId")
    @JsonBackReference
    private Room room;

    @ManyToOne
    @JoinColumn(name = "movieId")
    @JsonBackReference
    private Movie movie;

    @Column(name = "start_time")
    private ZonedDateTime startTime;

    @Column(name = "end_time")
    private ZonedDateTime endTime;

    @Column(name = "available_seat")
    private int availableSeat;

    @Column(name = "base_price")
    private BigDecimal basePrice;
}
