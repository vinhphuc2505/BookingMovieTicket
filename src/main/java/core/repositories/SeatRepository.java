package core.repositories;

import core.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID> {
    boolean existsBySeatNumber(String seatNumber);

    @Query("SELECT s FROM Seat s WHERE s.room.roomId = :roomId")
    List<Seat> findAllByRoomId(@Param("roomId")UUID roomId);
}
