package core.services;


import core.dto.request.movie.MovieCreateRequest;
import core.dto.request.movie.MovieUpdateRequest;
import core.dto.response.MovieResponse;
import core.dto.response.PageResponse;
import core.entities.Movie;
import core.exceptions.AppException;
import core.exceptions.ErrorCode;
import core.mapper.MovieMapper;
import core.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public PageResponse<MovieResponse> searchMovies(int page, int size, String title) {
        Pageable pageable = PageRequest.of(page -1, size, Sort.by("movieId").descending());

        Page<Movie> moviePage = movieRepository.searchByTitle(title, pageable);

        List<MovieResponse> movieResponses = moviePage.getContent().stream()
                .map(movieMapper::toMovieResponse)
                .toList();

        return PageResponse.<MovieResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(moviePage.getTotalPages())
                .totalElements(moviePage.getTotalElements())
                .data(movieResponses)
                .build();
    }

    @Override
    public PageResponse<MovieResponse> getMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("movieId").descending());

        Page<Movie> moviePage = movieRepository.findAll(pageable);

        List<MovieResponse> movieResponses = moviePage.getContent().stream()
                .map(movieMapper::toMovieResponse)
                .toList();


        return PageResponse.<MovieResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(moviePage.getTotalPages())
                .totalElements(moviePage.getTotalElements())
                .data(movieResponses)
                .build();
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
