package core.controllers;


import core.dto.request.movie.MovieCreateRequest;
import core.dto.request.movie.MovieUpdateRequest;
import core.dto.response.ApiResponse;
import core.dto.response.MovieResponse;
import core.dto.response.PageResponse;
import core.services.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public ApiResponse<MovieResponse> create(@RequestBody @Valid MovieCreateRequest request){
        return ApiResponse.<MovieResponse>builder()
                .code(201)
                .result(movieService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<MovieResponse>> getMovies(@RequestParam(name = "page", defaultValue = "1") int page,
                                                              @RequestParam(name = "size", defaultValue = "10") int size){
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .code(200)
                .result(movieService.getMovies(page, size))
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<MovieResponse>> searchMovies(@RequestParam(value = "title", required = false) String title,
                                                         @RequestParam(name = "page", defaultValue = "1") int page,
                                                         @RequestParam(name = "size", defaultValue = "10") int size){
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .code(200)
                .result(movieService.searchMovies(page, size, title))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<MovieResponse> update(@PathVariable("id")UUID id, @RequestBody @Valid MovieUpdateRequest request){
        return ApiResponse.<MovieResponse>builder()
                .code(200)
                .result(movieService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable("id")UUID id){
        movieService.delete(id);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Movie has been deleted")
                .build();
    }


}








