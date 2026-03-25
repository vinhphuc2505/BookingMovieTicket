package core.auth;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import core.dto.JwtInfo;
import core.entities.User;
import core.repositories.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @NonFinal
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    private final RedisTokenRepository redisTokenRepository;

    @Override
    public String generateAccessToken(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Date issueTime = new Date();
        Date expirationTime = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserId().toString())
                .issuer("booking.com")
                .jwtID(UUID.randomUUID().toString())
                .issueTime(issueTime)
                .expirationTime(expirationTime)
                .claim("scope", user.getRole())
                .claim("type", "Access")
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));

        return jwsObject.serialize();
    }

    @Override
    public String generateRefreshToken(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Date issueTime = new Date();
        Date expirationTime = Date.from(Instant.now().plus(7, ChronoUnit.DAYS));

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserId().toString())
                .issuer("booking.com")
                .jwtID(UUID.randomUUID().toString())
                .issueTime(issueTime)
                .expirationTime(expirationTime)
                .claim("scope", user.getRole())
                .claim("type", "Refresh")
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));

        return jwsObject.serialize();
    }

    @Override
    public boolean verifyToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        if (expiredTime.before(new Date())){
            throw new RuntimeException("Token đã hết hạn");
        }

        var jwtId = signedJWT.getJWTClaimsSet().getJWTID();

        if (redisTokenRepository.existsById(jwtId)){
            throw new RuntimeException("Token invalid");
        }

        return signedJWT.verify(new MACVerifier(SIGNER_KEY.getBytes()));
    }

    @Override
    public JwtInfo parseToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Date issueTime = signedJWT.getJWTClaimsSet().getIssueTime();
        Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        return JwtInfo.builder()
                .jwtId(jwtId)
                .issueTime(issueTime)
                .expiredTime(expiredTime)
                .build();
    }
}














