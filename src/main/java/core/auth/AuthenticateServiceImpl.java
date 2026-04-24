package core.auth;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import core.dto.JwtInfo;
import core.dto.TokenPayload;
import core.dto.request.user.AuthenticationRequest;
import core.dto.request.user.RefreshTokenRequest;
import core.dto.response.AuthenticationResponse;
import core.entities.RedisToken;
import core.entities.User;
import core.exceptions.AppException;
import core.exceptions.ErrorCode;
import core.repositories.RedisTokenRepository;
import core.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthenticateServiceImpl implements AuthenticateService{

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final RedisTokenRepository redisTokenRepository;

    private final UserRepository userRepository;

    private final String TYPE_NAME = "REFRESH";


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

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshToken) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(refreshToken.getRefreshToken());

        String tokenType = signedJWT.getJWTClaimsSet().getClaim("type").toString();
        String userId = signedJWT.getJWTClaimsSet().getSubject();
        String jwtIdRefreshToken = signedJWT.getJWTClaimsSet().getJWTID();

        if(tokenType == null || !tokenType.equalsIgnoreCase(TYPE_NAME)){
            throw new RuntimeException("Invalid token type");
        }

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if(!redisTokenRepository.existsById(jwtIdRefreshToken)){
            throw new RuntimeException("Refresh token has been revoked or expired");
        }

        redisTokenRepository.deleteById(jwtIdRefreshToken);

        TokenPayload refreshTokenNew = jwtService.generateRefreshToken(user);
        TokenPayload accessToken = jwtService.generateAccessToken(user, refreshTokenNew.getJwtId());

        RedisToken redisRefreshToken = RedisToken.builder()
                .jwtId(refreshTokenNew.getJwtId())
                .expiredTime(refreshTokenNew.getExpiredTime().getTime())
                .build();

        redisTokenRepository.save(redisRefreshToken);

        return AuthenticationResponse.builder()
                .authenticate(true)
                .accessToken(accessToken.getToken())
                .refreshToken(refreshTokenNew.getToken())
                .build();
    }
}












