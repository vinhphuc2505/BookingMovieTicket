package core.mapper;


import core.dto.request.user.UserCreateRequest;
import core.dto.request.user.UserUpdateRequest;
import core.dto.response.UserResponse;
import core.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreateRequest request);

    UserResponse toUserResponse(User user);

    List<UserResponse> toUserResponse(List<User> users);

    @Mapping(target = "role", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
