package core.controllers;


import core.dto.request.showtimeseat.ShowTimeSeatCreateRequest;
import core.dto.request.showtimeseat.ShowTimeSeatHoldRequest;
import core.dto.response.ApiResponse;
import core.dto.response.PageResponse;
import core.dto.response.ShowTimeSeatResponse;
import core.services.ShowTimeSeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/showtimeseats")
@RequiredArgsConstructor
public class ShowTimeSeatController {

    private final ShowTimeSeatService showTimeSeatService;

    @PostMapping
    public ApiResponse<List<ShowTimeSeatResponse>> create(@RequestBody @Valid ShowTimeSeatCreateRequest request){
        return ApiResponse.<List<ShowTimeSeatResponse>>builder()
                .code(201)
                .result(showTimeSeatService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<ShowTimeSeatResponse>> findAllByShowTime(
            @RequestParam(value = "showTimeId")UUID id,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        return ApiResponse.<PageResponse<ShowTimeSeatResponse>>builder()
                .code(200)
                .result(showTimeSeatService.findAllByShowTime(id, page, size))
                .build();
    }

    @PutMapping
    public ApiResponse<List<ShowTimeSeatResponse>> holdingSeat(@RequestBody @Valid ShowTimeSeatHoldRequest request){
        return ApiResponse.<List<ShowTimeSeatResponse>>builder()
                .code(200)
                .result(showTimeSeatService.holdingSeat(request))
                .build();
    }

    @PutMapping("/v2")
    public ApiResponse<List<ShowTimeSeatResponse>> holdingSeatV2(@RequestBody @Valid ShowTimeSeatHoldRequest request){
        return ApiResponse.<List<ShowTimeSeatResponse>>builder()
                .code(200)
                .result(showTimeSeatService.holdingSeatV2(request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable("id") UUID id){
        showTimeSeatService.delete(id);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Showtime seat has been deleted")
                .build();
    }

}








