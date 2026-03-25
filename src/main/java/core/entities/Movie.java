package core.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movie")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "movie_id")
    private UUID movieId;

    @Column(name = "title")
    private String title;

    @Column(name = "duration")
    private int duration;

    @Column(name = "genre")
    private String genre;

    @Column(name = "rating")
    private int rating;

    @Column(name = "description")
    private String description;

    @Column(name = "release_at")
    private LocalDate releaseAt;

    @OneToMany(mappedBy = "movie")
    @JsonManagedReference
    private List<ShowTime> showTimeList;

}
