package core.auth;


import com.nimbusds.jose.JOSEException;
import core.dto.JwtInfo;
import core.dto.request.user.AuthenticationRequest;
import core.dto.response.AuthenticationResponse;
import core.entities.RedisToken;
import core.entities.User;
import core.repositories.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.text.ParseException;
import java.util.Date;


@Service
@RequiredArgsConstructor
public class AuthenticateServiceImpl implements AuthenticateService{

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final RedisTokenRepository redisTokenRepository;


    @Override
    public AuthenticationResponse login(AuthenticationRequest request) throws JOSEException {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(request.getLoginName(), request.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        User user = (User) authentication.getPrincipal();

        return AuthenticationResponse.builder()
                .authenticate(true)
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }

    @Override
    public void logout(String token) throws ParseException {
        JwtInfo jwtInfo = jwtService.parseToken(token);

        String jwtId = jwtInfo.getJwtId();
        Date expiredTime = jwtInfo.getExpiredTime();

        if (expiredTime.before(new Date())){
            throw new RuntimeException("Token expired");
        }

        RedisToken redisToken = RedisToken.builder()
                .jwtId(jwtId)
                .expiredTime(expiredTime.getTime() - new Date().getTime())
                .build();

        redisTokenRepository.save(redisToken);
    }
}












