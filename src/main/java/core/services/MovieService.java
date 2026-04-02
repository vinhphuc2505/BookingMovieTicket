package core.services;

import core.dto.request.movie.MovieCreateRequest;
import core.dto.request.movie.MovieUpdateRequest;
import core.dto.response.MovieResponse;
import core.dto.response.PageResponse;


import java.util.UUID;

public interface MovieService {
    MovieResponse create(MovieCreateRequest request);

    PageResponse<MovieResponse> searchMovies(int page, int size, String title);

    PageResponse<MovieResponse> getMovies(int page, int size);

    MovieResponse update(UUID id, MovieUpdateRequest request);

    void delete(UUID id);
}
