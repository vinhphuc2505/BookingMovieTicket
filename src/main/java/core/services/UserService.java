package core.services;


import core.dto.request.user.UserCreateRequest;
import core.dto.request.user.UserUpdateRequest;
import core.dto.response.UserResponse;


import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse create(UserCreateRequest request);

    List<UserResponse> getUsers();

    UserResponse update(UUID id, UserUpdateRequest request);

    void delete(UUID id);
}
