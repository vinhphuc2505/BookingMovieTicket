package core.auth;


import com.nimbusds.jose.JOSEException;
import core.dto.JwtInfo;
import core.dto.TokenPayload;
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

        TokenPayload refreshToken = jwtService.generateRefreshToken(user);
        TokenPayload accessToken = jwtService.generateAccessToken(user, refreshToken.getJwtId());

        RedisToken redisRefreshToken = RedisToken.builder()
                .jwtId(refreshToken.getJwtId())
                .expiredTime(refreshToken.getExpiredTime().getTime())
                .build();

        redisTokenRepository.save(redisRefreshToken);

        return AuthenticationResponse.builder()
                .authenticate(true)
                .accessToken(accessToken.getToken())
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Override
    public void logout(String token) throws ParseException {
        JwtInfo jwtInfo = jwtService.parseToken(token);

        String jwtId = jwtInfo.getJwtId();
        String jwtIdRefreshToken = jwtInfo.getJwtIdRefreshToken();
        Date expiredTime = jwtInfo.getExpiredTime();

        if (expiredTime.before(new Date())){
            throw new RuntimeException("Token expired");
        }

        RedisToken redisToken = RedisToken.builder()
                .jwtId(jwtId)
                .expiredTime(expiredTime.getTime() - new Date().getTime())
                .build();

        redisTokenRepository.save(redisToken);

        redisTokenRepository.deleteById(jwtIdRefreshToken);
    }
}












