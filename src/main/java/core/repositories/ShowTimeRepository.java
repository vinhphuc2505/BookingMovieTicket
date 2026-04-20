package core.repositories;

import core.entities.ShowTime;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, UUID> {

    Page<ShowTime> findByStartTimeBetween(ZonedDateTime start, ZonedDateTime end, Pageable pageable);

    Page<ShowTime> findByStartTimeBetweenAndMovie_MovieId(ZonedDateTime start, ZonedDateTime end, UUID movieId, Pageable pageable);


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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ShowTime s WHERE s.id = :id")
    Optional<ShowTime> findByIdWithLock(UUID id);
}
