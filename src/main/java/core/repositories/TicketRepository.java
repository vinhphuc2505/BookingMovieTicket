package core.repositories;

import core.entities.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    @Query("SELECT t FROM Ticket t WHERE t.user.userId = :userId")
    Optional<Ticket> findByUserId(UUID userId);

    @Query("SELECT t FROM Ticket t WHERE t.user.userId = :userId")
    Page<Ticket> findAllTicketByUser(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT t FROM Ticket t " +
            "JOIN t.showTimeSeat sts " +
            "JOIN sts.showTime st " +
            "WHERE t.isReminded = false " +
            "AND st.startTime >= :startTimeStart " +
            "AND st.startTime <= :startTimeEnd")
    List<Ticket> findTicketsToRemind(@Param("startTimeStart") ZonedDateTime startTimeStart,
                                     @Param("startTimeEnd") ZonedDateTime startTimeEnd);
}
