package uz.demo.card_procession_app.configuration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;

import java.security.Key;
import java.util.Date;

@Configuration
@RequiredArgsConstructor
public class JwtProvider {

    private final MessageSource messageSource;
    private long expireTime = 1000 * 60 * 60 * 24;
    private String secret = "ThisIsASecretKeyForJwtTokenGeneration_32bytes!";

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .claim("role", role)
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getKey())
                .compact();
    }
    private Key getKey() {
        byte[] bytes = secret.getBytes();
        return Keys.hmacShaKeyFor(bytes);
    }

    public String getPhoneNumberFromToken(String token) {
        try {
            String phoneNumber =
                    Jwts.parserBuilder()
                            .setSigningKey(getKey())
                            .build()
                            .parseClaimsJws(token)
                            .getBody()
                            .getSubject();
            return phoneNumber;
        } catch (Exception e) {
            throw new  RuntimeException(messageSource.getMessage("error.phone_number_from_token", null, LocaleContextHolder.getLocale()) +": "+ e);
        }
    }
}
