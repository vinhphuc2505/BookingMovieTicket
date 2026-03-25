package core.mapper;


import core.dto.request.movie.MovieCreateRequest;
import core.dto.request.movie.MovieUpdateRequest;
import core.dto.response.MovieResponse;
import core.entities.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    Movie toMovie(MovieCreateRequest request);

    MovieResponse toMovieResponse(Movie movie);

    List<MovieResponse> toMovieResponse(List<Movie> movies);

    void updateMovie(@MappingTarget Movie movie, MovieUpdateRequest request);
}
