package core.controllers;


import core.dto.request.seat.SeatCreateRequest;
import core.dto.response.ApiResponse;
import core.dto.response.SeatResponse;
import core.services.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;


    @PostMapping
    public ApiResponse<SeatResponse> create(@RequestBody @Valid SeatCreateRequest request){
        return ApiResponse.<SeatResponse>builder()
                .code(201)
                .result(seatService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<SeatResponse>> findAllSeatByRoom(@RequestParam(value = "roomId", required = false)UUID roomId){
        return ApiResponse.<List<SeatResponse>>builder()
                .code(200)
                .result(seatService.getSeatByRoom(roomId))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable("id") UUID id){
        seatService.delete(id);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Seat has been deleted")
                .build();
    }

}




