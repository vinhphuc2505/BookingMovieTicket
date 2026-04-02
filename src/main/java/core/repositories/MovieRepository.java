package core.repositories;

import core.entities.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {

    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Movie> searchByTitle(@Param("title") String title, Pageable pageable);

    @Query("SELECT m FROM Movie m")
    Page<Movie> findAllMoviePageAble(Pageable pageable);

}
