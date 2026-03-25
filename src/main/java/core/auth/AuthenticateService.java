package core.auth;

import com.nimbusds.jose.JOSEException;
import core.dto.request.user.AuthenticationRequest;
import core.dto.response.AuthenticationResponse;

import java.text.ParseException;

public interface AuthenticateService {
    AuthenticationResponse login(AuthenticationRequest request) throws JOSEException;

    void logout(String token) throws ParseException;
}
