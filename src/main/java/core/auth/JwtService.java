package core.auth;

import com.nimbusds.jose.JOSEException;
import core.dto.JwtInfo;
import core.dto.TokenPayload;
import core.entities.User;

import java.text.ParseException;

public interface JwtService {
    TokenPayload generateAccessToken(User user, String jwtIdRefreshToken) throws JOSEException;

    TokenPayload generateRefreshToken(User user) throws JOSEException;

    boolean verifyToken(String token) throws ParseException, JOSEException;

    JwtInfo parseToken(String token) throws ParseException;

}
