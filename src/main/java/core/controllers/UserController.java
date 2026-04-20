package core.controllers;


import core.dto.request.user.UserCreateRequest;
import core.dto.request.user.UserUpdateRequest;
import core.dto.response.ApiResponse;
import core.dto.response.PageResponse;
import core.dto.response.UserResponse;
import core.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<UserResponse> create(@RequestBody @Valid UserCreateRequest request){
        return ApiResponse.<UserResponse>builder()
                .code(201)
                .result(userService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<UserResponse>> getUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                                                            @RequestParam(value = "size", defaultValue = "10") int size){
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .code(200)
                .result(userService.getUsers(page, size))
                .build();
    }

    @PutMapping
    public ApiResponse<UserResponse> update(@RequestBody @Valid UserUpdateRequest request){
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .result(userService.update(request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable("id")UUID id){
        userService.delete(id);
        return ApiResponse.<String>builder()
                .code(200)
                .message("User has been deleted")
                .build();
    }

}








