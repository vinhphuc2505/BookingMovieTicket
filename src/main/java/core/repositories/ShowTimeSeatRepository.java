package core.repositories;


import core.entities.Seat;
import core.entities.ShowTime;
import core.entities.ShowTimeSeat;
import core.enums.SeatStatus;
import core.enums.StatusReason;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShowTimeSeatRepository extends JpaRepository<ShowTimeSeat, UUID> {
    boolean existsBySeat(Seat seat);

    boolean existsByShowTime(ShowTime showTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ShowTimeSeat s WHERE s.showTimeSeatId IN :showTimeSeatIds")
    List<ShowTimeSeat> findAllByIdAndLockSeat(@Param("showTimeSeatIds") List<UUID> showTimeSeatIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ShowTimeSeat s WHERE s.showTimeSeatId = :showTimeSeatId")
    ShowTimeSeat findAndUpdate(@Param("showTimeSeatId") UUID showTimeSeatId);

    @Query("SELECT sts FROM ShowTimeSeat sts " +
            "JOIN FETCH sts.showTime st " +
            "WHERE st.showTimeId = :showTimeId")
    Page<ShowTimeSeat> findAllByShowTime(@Param("showTimeId") UUID showTimeId, Pageable pageable);

    @Modifying
    @Query("UPDATE ShowTimeSeat s SET s.status = 'AVAILABLE', s.holdExpiresAt = null " +
            "WHERE s.status = 'HOLDING' AND s.holdExpiresAt < :now")
    int releaseExpiredHoldingSeats(ZonedDateTime now);

    @Modifying
    @Query("DELETE FROM ShowTimeSeat s WHERE s.status IN ('AVAILABLE', 'HOLDING') " +
            "AND s.showTime.startTime < :now")
    int deleteObsoleteSeats(ZonedDateTime now);
}
