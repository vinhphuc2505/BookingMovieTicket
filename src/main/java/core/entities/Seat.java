package core.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "seat")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "seat_id", updatable = false, nullable = false)
    private UUID seatId;

    @ManyToOne
    @JoinColumn(name = "roomId")
    @JsonBackReference
    private Room room;

    @Column(name = "seat_number")
    private String seatNumber;
}
