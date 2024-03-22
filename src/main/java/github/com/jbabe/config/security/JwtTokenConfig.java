package github.com.jbabe.config.security;

import github.com.jbabe.repository.redis.RedisTokenRepository;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.authAccount.RedisUtil;
import github.com.jbabe.service.exception.NotFoundException;
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

    @Value("${jwt.valid-time.access-token}")
    private String accessTokenValidMillisecond; // access token 유효기간

    @Value("${jwt.valid-time.refresh-token}")
    private String refreshTokenValidMillisecond; // refresh token 유효기간




    @PostConstruct
    public void setUp() { // secretKey 인코딩
        key = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public static final String EXCEPTION_HEADER_NAME = "AUTH_EXCEPTION";

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("AccessToken");
    }

    public String createAccessToken(String email, String role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + Long.parseLong(accessTokenValidMillisecond) + 1000*60*60*9);
        User user = userJpa.findByEmail(email).orElseThrow(() -> new NotFoundException("해당 이메일로 유저를 찾을 수 없습니다.", email));

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setAudience(user.getName())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public String createRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Long.parseLong(refreshTokenValidMillisecond)))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public String regenerateRefreshToken(String refreshToken) {
        Date expirationTime = Jwts.parser().setSigningKey(key).parseClaimsJws(refreshToken).getBody().getExpiration();
        Date now = new Date();
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public boolean validateToken(String jwtToken) {
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken).getBody();
        Date now = new Date();
        boolean isLogoutToken = redisTokenRepository.getBlacklist(claims.getSubject())
                .contains(jwtToken);
        return !claims.getExpiration().before(now) && !isLogoutToken;
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
        redisUtil.setDataExpire(accessToken, refreshToken, Long.parseLong(refreshTokenValidMillisecond));
    }

    //토큰 만료시간 파싱
    public Date getTokenValidity(String refreshToken) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(refreshToken).getBody().getExpiration();
    }
}
