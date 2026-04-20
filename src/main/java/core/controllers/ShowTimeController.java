package core.controllers;


import core.dto.request.showtime.ShowTimeCreateRequest;
import core.dto.request.showtime.ShowTimeUpdateRequest;
import core.dto.response.ApiResponse;
import core.dto.response.PageResponse;
import core.dto.response.ShowTimeResponse;
import core.services.ShowTimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/showtimes")
@RequiredArgsConstructor
public class ShowTimeController {

    private final ShowTimeService showTimeService;

    @PostMapping
    public ApiResponse<ShowTimeResponse> create(@RequestBody @Valid ShowTimeCreateRequest request){
        return ApiResponse.<ShowTimeResponse>builder()
                .code(201)
                .result(showTimeService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<ShowTimeResponse>> findShowTimeByDate(
            @RequestParam(value = "date", required = false)LocalDate date,
            @RequestParam(value = "page", defaultValue = "1")int page,
            @RequestParam(value = "size", defaultValue = "10")int size){
        return ApiResponse.<PageResponse<ShowTimeResponse>>builder()
                .code(200)
                .result(showTimeService.findShowTimeByDate(date, page, size))
                .build();
    }

    @GetMapping("/movies")
    public ApiResponse<PageResponse<ShowTimeResponse>> findShowTimeByDate(
            @RequestParam(value = "date", required = false)LocalDate date,
            @RequestParam(value = "movieId", required = false) UUID movieId,
            @RequestParam(value = "page", defaultValue = "1")int page,
            @RequestParam(value = "size", defaultValue = "10")int size){
        return ApiResponse.<PageResponse<ShowTimeResponse>>builder()
                .code(200)
                .result(showTimeService.findShowTimeByDateAndMovie(date, movieId, page, size))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ShowTimeResponse> update(@PathVariable("id")UUID id,
                                                @RequestBody @Valid ShowTimeUpdateRequest request){
        return ApiResponse.<ShowTimeResponse>builder()
                .code(200)
                .result(showTimeService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable("id") UUID id){
        showTimeService.delete(id);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Show time has been deleted")
                .build();
    }

}













