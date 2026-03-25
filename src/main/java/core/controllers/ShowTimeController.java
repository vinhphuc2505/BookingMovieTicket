package core.controllers;


import core.dto.request.showtime.ShowTimeCreateRequest;
import core.dto.request.showtime.ShowTimeUpdateRequest;
import core.dto.response.ApiResponse;
import core.dto.response.ShowTimeResponse;
import core.services.ShowTimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/showtime")
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
    public ApiResponse<List<ShowTimeResponse>> findShowTimeByDate(
            @RequestParam(value = "date", required = false)LocalDate date){
        return ApiResponse.<List<ShowTimeResponse>>builder()
                .code(200)
                .result(showTimeService.findShowTimeByDate(date))
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













