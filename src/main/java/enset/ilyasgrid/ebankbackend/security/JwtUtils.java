package enset.ilyasgrid.ebankbackend.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    private final JwtEncoder encoder;
    
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    public JwtUtils(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generateToken(String username, Map<String, Object> claims) {
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(username)
                .claims(existingClaims -> existingClaims.putAll(claims))
                .build();
        return encoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }
}