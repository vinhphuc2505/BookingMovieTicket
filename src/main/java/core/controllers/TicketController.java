package core.controllers;


import core.dto.request.ticket.TicketCreateRequest;
import core.dto.request.ticket.TicketUpdateRequest;
import core.dto.response.ApiResponse;
import core.dto.response.PageResponse;
import core.dto.response.TicketResponse;
import core.services.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ApiResponse<List<TicketResponse>> create(@RequestBody @Valid TicketCreateRequest request){
        return ApiResponse.<List<TicketResponse>>builder()
                .code(200)
                .result(ticketService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<TicketResponse>> findAllTicketByUser(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        return ApiResponse.<PageResponse<TicketResponse>>builder()
                .code(200)
                .result(ticketService.findAllTicketByUser(page, size))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<TicketResponse> update(@PathVariable("id") UUID id, @RequestParam @Valid TicketUpdateRequest request){
        return ApiResponse.<TicketResponse>builder()
                .code(200)
                .result(ticketService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable("id") UUID id){
        ticketService.delete(id);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Ticket has been deleted")
                .build();
    }


}









