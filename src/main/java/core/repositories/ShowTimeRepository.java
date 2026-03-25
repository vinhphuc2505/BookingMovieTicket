package core.repositories;

import core.entities.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface ShowTimeRepository extends JpaRepository<ShowTime, UUID> {

    List<ShowTime> findByStartTimeBetween(ZonedDateTime start, ZonedDateTime end);

    @Query("SELECT COUNT(s) > 0 FROM ShowTime s " +
            "WHERE s.room.roomId = :roomId " +
            "AND s.startTime < :endTime " +
            "AND s.endTime > :startTime")
    boolean existsOverlap(@Param("roomId") UUID roomId,
                          @Param("startTime") ZonedDateTime startTime, @Param("endTime") ZonedDateTime endTime);

    @Query("SELECT COUNT(s) > 0 FROM ShowTime s " +
            "WHERE s.room.roomId = :roomId " +
            "AND s.startTime < :endTime " +
            "AND s.endTime > :startTime " +
            "AND s.showTimeId <> :excludeId")
    boolean existsOverlap(@Param("roomId") UUID roomId, @Param("startTime") ZonedDateTime startTime,
                          @Param("endTime") ZonedDateTime endTime, @Param("excludeId") UUID excludeId);
}
