package github.com.jbabe.config.security;

import github.com.jbabe.repository.redis.RedisTokenRepository;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.authAccount.RedisUtil;
import github.com.jbabe.service.exception.ExpiredJwtException;
import github.com.jbabe.service.exception.NotFoundException;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenConfig {
    private final UserJpa userJpa;

    private final UserDetailsService userDetailsService;
    private final RedisUtil redisUtil;
    private final RedisTokenRepository redisTokenRepository;

    @Value("${jwt.jwt-password.source}")
    private String secretKey;
    private String key;
    private final long accessTokenValidMillisecond = 1000*60*30L; // access token 유효기간 : 10분

    @Getter
    private final long refreshTokenValidMillisecond = 1000*60*60*24*3L; // refresh token 유효기간 : 24시간




    @PostConstruct
    public void setUp() { // secretKey 인코딩
        key = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public static final String EXCEPTION_HEADER_NAME = "AUTH_EXCEPTION";

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("AccessToken");
    }

    public String createAccessToken(String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenValidMillisecond);
        User user = userJpa.findByEmail(email).orElseThrow(() -> new NotFoundException("해당 이메일로 유저를 찾을 수 없습니다.", email));

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", user.getRole());
        return Jwts.builder()
                .setClaims(claims)
                .setAudience(new String(user.getName().getBytes(), StandardCharsets.UTF_8))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public String createRefreshToken(String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenValidMillisecond);
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

    }

    public String regenerateRefreshToken(String email, String refreshToken) {
        Date expirationTime = getTokenValidity(refreshToken);
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public boolean accessTokenValidate(String jwtToken) {
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken).getBody();
        Date now = new Date();
        boolean isLogoutToken = redisTokenRepository.getBlacklist(claims.getSubject())
                .contains(jwtToken);
        return !claims.getExpiration().before(now) && !isLogoutToken;
    }

    public Boolean refreshTokenValidate(String jwtToken) {
        try {
            Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken).getBody();
            Date now = new Date();
            return !claims.getExpiration().before(now);
        }catch (Exception e) {
            throw new ExpiredJwtException("refresh 토큰이 만료되었습니다.");
        }

    }

    public Authentication getAuthentication(String jwtToken) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserEmail(jwtToken));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserEmail(String jwtToken) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken).getBody().getSubject();
    }

    @Transactional
    public void saveRedisTokens(String accessToken, String refreshToken) {
        Date expirationTime = getTokenValidity(refreshToken);
        redisUtil.setDataExpire(accessToken, refreshToken, expirationTime.getTime());
    }

    //토큰 만료시간 파싱
    public Date getTokenValidity(String refreshToken) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(refreshToken).getBody().getExpiration();
    }
}
