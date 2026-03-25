package core.auth;

import com.nimbusds.jose.JOSEException;
import core.dto.JwtInfo;
import core.entities.User;

import java.text.ParseException;

public interface JwtService {
    String generateAccessToken(User user) throws JOSEException;

    String generateRefreshToken(User user) throws JOSEException;

    boolean verifyToken(String token) throws ParseException, JOSEException;

    JwtInfo parseToken(String token) throws ParseException;

}
