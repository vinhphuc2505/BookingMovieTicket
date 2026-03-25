package core.services;


import core.dto.request.movie.MovieCreateRequest;
import core.dto.request.movie.MovieUpdateRequest;
import core.dto.response.MovieResponse;
import core.entities.Movie;
import core.exceptions.AppException;
import core.exceptions.ErrorCode;
import core.mapper.MovieMapper;
import core.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService{

    private final MovieRepository movieRepository;

    private final MovieMapper movieMapper;


    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MovieResponse create(MovieCreateRequest request) {
        Movie movie = movieMapper.toMovie(request);

        return movieMapper.toMovieResponse(movieRepository.save(movie));
    }

    @Override
    public List<MovieResponse> searchMovies(String title) {
        return movieMapper.toMovieResponse(movieRepository.searchByTitle(title));
    }

    @Override
    public List<MovieResponse> getMovies() {
        return movieMapper.toMovieResponse(movieRepository.findAll());
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MovieResponse update(UUID id, MovieUpdateRequest request) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_EXISTED));

        movieMapper.updateMovie(movie, request);

        return movieMapper.toMovieResponse(movieRepository.save(movie));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(UUID id) {
        movieRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_EXISTED));
        movieRepository.deleteById(id);
    }
}
