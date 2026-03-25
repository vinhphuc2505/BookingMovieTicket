package core.services;

import core.dto.request.user.UserCreateRequest;
import core.dto.request.user.UserUpdateRequest;
import core.dto.response.UserResponse;
import core.entities.User;
import core.exceptions.AppException;
import core.exceptions.ErrorCode;
import core.mapper.UserMapper;
import core.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public UserResponse create(UserCreateRequest request) {
        if(userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail())){
            throw new AppException(ErrorCode.USER_OR_EMAIL_EXISTED);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return userMapper.toUserResponse(userRepository.findAll());
    }

    @Override
    public UserResponse update(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(UUID id) {
        userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userRepository.deleteById(id);
    }
}
