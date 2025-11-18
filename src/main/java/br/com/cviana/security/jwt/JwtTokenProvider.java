package br.com.cviana.security.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.com.cviana.data.dto.security.TokenDTO;
import br.com.cviana.exceptions.InvalidJwtAuthenticationException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey;

    @Value("${security.jwt.token.expiration-time:3600000}")
    private long expirationTime;

    @Autowired
    private UserDetailsService userDetailsService;

    Algorithm algorithm = null;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        algorithm = Algorithm.HMAC256(secretKey.getBytes());
    }

    public TokenDTO createAccessToken(String username, List<String> roles) {
        Date now = new Date();
        Date until = new Date(now.getTime()+expirationTime);
        String accessToken = getAccessToken(username, roles, now, until);
        String refreshToken = getRefreshToken(username, roles, now);
        return new TokenDTO(username, true, now, until, accessToken, refreshToken);
    }

    public TokenDTO refreshToken(String refreshToken) {
        if(refreshToken == null) 
            throw new InvalidJwtAuthenticationException("Invalid JWT refresh token!");
        
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedToken = verifier.verify(refreshToken);

        String username = decodedToken.getSubject();
        List<String> roles = decodedToken.getClaim("roles").asList(String.class);
        return createAccessToken(username, roles);
    }

    private String getAccessToken(String username, List<String> roles, Date now, Date until) {
        String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return JWT.create()
            .withClaim("roles", roles)
            .withIssuedAt(now)
            .withExpiresAt(until)
            .withSubject(username)
            .withIssuer(issuerUrl)
            .sign(algorithm);
    }

    private String getRefreshToken(String username, List<String> roles, Date now) {
        Date refreshTokenExpiration = new Date(now.getTime()+(expirationTime*3));
        return JWT.create()
            .withClaim("roles", roles)
            .withIssuedAt(now)
            .withExpiresAt(refreshTokenExpiration)
            .withSubject(username)
            .sign(algorithm);
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decoded = decodedToken(token);
        UserDetails userdetails = this.userDetailsService.loadUserByUsername(decoded.getSubject());
        return new UsernamePasswordAuthenticationToken(userdetails, "", userdetails.getAuthorities());
    }

    private DecodedJWT decodedToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedToken = verifier.verify(token);
        return decodedToken;
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if(StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) return bearerToken.substring("Bearer ".length());
        //else throw new InvalidJwtAuthenticationException("Invalid JWT token!");
        return null;
    }

    public boolean validateToken(String token) {
        DecodedJWT decoded = decodedToken(token);
        try {
            if(decoded.getExpiresAt().before(new Date())) return false;
        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token!");
        }
        return true;
    }
}
