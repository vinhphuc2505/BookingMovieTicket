package core.config;


import com.nimbusds.jose.JOSEException;
import core.auth.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtDecoderConfig implements JwtDecoder {

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    private final JwtService jwtService;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;


    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            if (!jwtService.verifyToken(token)){
                throw new RuntimeException();
            }

            if (Objects.isNull(nimbusJwtDecoder)){
                SecretKey secretKey = new
                        SecretKeySpec(SIGNER_KEY.getBytes(StandardCharsets.UTF_8), "HS512");
                nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey)
                        .macAlgorithm(MacAlgorithm.HS512)
                        .build();
            }

        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }

        return nimbusJwtDecoder.decode(token);
    }
}
