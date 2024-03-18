package github.com.jbabe.config.security;

import github.com.jbabe.service.authAccount.RedisUtil;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenConfig {

    private final UserDetailsService userDetailsService;
    private final RedisUtil redisUtil;

    @Value("${jwt.jwt-password.source}")
    private String secretKey;

    private String key;

    @Value("${jwt.valid-time.access-token}")
    private String accessTokenValidMillisecond; // access token 유효기간

    @Value("${jwt.valid-time.refresh-token}")
    private String refreshTokenValidMillisecond; // refresh token 유효기간




    @PostConstruct
    public void setUp() { // secretKey 인코딩
        key = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("AccessToken");
    }

    public String createAccessToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Long.parseLong(accessTokenValidMillisecond)))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public String createRefreshToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Long.parseLong(refreshTokenValidMillisecond)))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

    }

    public boolean validateToken(String jwtToken) {
        try {
            Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken).getBody();
            Date now = new Date();
            return !claims.getExpiration().before(now);
        }catch (ExpiredJwtException e) {
            log.error(e.getMessage());
            throw new github.com.jbabe.service.exception.ExpiredJwtException("토큰이 만료되었습니다.");
        }catch (JwtException e) {
            log.error(e.getMessage());
            throw new github.com.jbabe.service.exception.JwtException("Jwt 인증 오류");
        }
    }

    public Authentication getAuthentication(String jwtToken) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserEmail(jwtToken));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String getUserEmail(String jwtToken) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken).getBody().getSubject();
    }

    @Transactional
    public void saveRedisTokens(String accessToken, String refreshToken) {
        redisUtil.setDataExpire(accessToken, refreshToken, Long.parseLong(refreshTokenValidMillisecond));
    }
}
