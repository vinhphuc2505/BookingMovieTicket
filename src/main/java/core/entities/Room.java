package core.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    @Column(name = "room_id", updatable = false, nullable = false)
    private UUID roomId;

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Column(name = "total_seats")
    private int totalSeats;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Seat> seats;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ShowTime> showTimeList;
}
