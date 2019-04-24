package org.desarrolladorslp.workshops.springboot.security.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public final class TokenProvider {

    private final JwtProperties jwtProperties;

    public TokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
    private final String AUTHORITIES_KEY = "auth";

    public boolean isValidToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtProperties.getSecretKey()).parseClaimsJws(token);
            return true;
        } catch(ExpiredJwtException e) {
            log.info("Expired JWT token");
        } catch(UnsupportedJwtException e) {
            log.info("Unsupported JWT token");
        } catch(MalformedJwtException e) {
            log.info("Malformed JWT token");
        } catch(SignatureException e) {
            log.info("Invalid JWT token");
        } catch(IllegalArgumentException e) {
            log.info("Invalid argument for JWT token");
        }
        return false;
    }

    public String refreshToken(String token) {

        final Date newIssuedAt = new Date();
        final Date newExpiration = new Date(newIssuedAt.getTime() + (1000*jwtProperties.getExpireLength()));

        final Claims claims = Jwts.parser().setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token).getBody();
        claims.setIssuedAt(newIssuedAt);
        claims.setExpiration(newExpiration);

        return Jwts.builder()
                .setHeaderParam("alg", SIGNATURE_ALGORITHM.getValue())
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .signWith(SIGNATURE_ALGORITHM, jwtProperties.getSecretKey())
                .compact();

    }

    public Collection<? extends GrantedAuthority> getAuthorities(String token) {
        final Claims claims = Jwts.parser().setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token).getBody();

        final Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return authorities;
    }

    public String getSubject(String token) {
        return Jwts.parser().setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token).getBody().getSubject();
    }

}
