package core.controllers;


import core.dto.request.room.RoomCreateRequest;
import core.dto.request.room.RoomUpdateRequest;
import core.dto.response.ApiResponse;
import core.dto.response.PageResponse;
import core.dto.response.RoomResponse;
import core.services.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping
    public ApiResponse<RoomResponse> create(@RequestBody @Valid RoomCreateRequest request){
        return ApiResponse.<RoomResponse>builder()
                .code(201)
                .result(roomService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<RoomResponse>> getRoom(@RequestParam(value = "page", defaultValue = "1")int page,
                                                           @RequestParam(value = "size", defaultValue = "10")int size){
        return ApiResponse.<PageResponse<RoomResponse>>builder()
                .code(200)
                .result(roomService.getRoom(page, size))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<RoomResponse> update(@PathVariable("id")UUID id, @RequestBody @Valid RoomUpdateRequest request){
        return ApiResponse.<RoomResponse>builder()
                .code(200)
                .result(roomService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable("id") UUID id){
        roomService.delete(id);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Room has been delete")
                .build();
    }

}









