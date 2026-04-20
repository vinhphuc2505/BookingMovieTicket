package core.services;


import core.dto.request.user.UserCreateRequest;
import core.dto.request.user.UserUpdateRequest;
import core.dto.response.PageResponse;
import core.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse create(UserCreateRequest request);

    PageResponse<UserResponse> getUsers(int page, int size);

    UserResponse update(UserUpdateRequest request);

    void delete(UUID id);
}
