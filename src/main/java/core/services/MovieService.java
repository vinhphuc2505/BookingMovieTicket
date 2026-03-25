package core.services;

import core.dto.request.movie.MovieCreateRequest;
import core.dto.request.movie.MovieUpdateRequest;
import core.dto.response.MovieResponse;

import java.util.List;
import java.util.UUID;

public interface MovieService {
    MovieResponse create(MovieCreateRequest request);

    List<MovieResponse> searchMovies(String title);

    List<MovieResponse> getMovies();

    MovieResponse update(UUID id, MovieUpdateRequest request);

    void delete(UUID id);
}
