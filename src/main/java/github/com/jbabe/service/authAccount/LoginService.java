package github.com.jbabe.service.authAccount;

import github.com.jbabe.config.security.JwtTokenConfig;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.ExpiredJwtException;
import github.com.jbabe.service.exception.NotAcceptableException;
import github.com.jbabe.service.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenConfig jwtTokenConfig;
    private final RedisUtil redisUtil;
    private final UserJpa userJpa;

    @Transactional
    public String refreshToken(String expiredAccessToken) {
        try {
            // redis에 accessToken 에 맞는 refreshToken 이 없다면 에러
            // validateToken이 false일 경우 에러
            String refreshToken = redisUtil.getData(expiredAccessToken);
            if (jwtTokenConfig.validateToken(refreshToken)) {
                String userEmail = jwtTokenConfig.getAuthentication(refreshToken).getName();

                String newAccessToken = jwtTokenConfig.createAccessToken(userEmail);
                String newRefreshToken = jwtTokenConfig.createRefreshToken(userEmail);

                jwtTokenConfig.saveRedisTokens(newAccessToken, newRefreshToken);
                redisUtil.deleteData(expiredAccessToken);
                return newAccessToken;
            }else{
                throw new ExpiredJwtException("refresh 토큰이 만료되었습니다.");
            }
        }catch (RedisSystemException e) {
            throw new ExpiredJwtException("refresh 토큰이 만료되었습니다.", "");
        }
    }

    @Transactional
    public String login(String email, String password) {

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = jwtTokenConfig.createAccessToken(email);
            String refreshToken = jwtTokenConfig.createRefreshToken(email);
            jwtTokenConfig.saveRedisTokens(accessToken, refreshToken); // redis에 Token 저장

            return accessToken;
        }catch (Exception e) {
            e.printStackTrace();
            throw new NotAcceptableException("로그인 할 수 없습니다.", email);
        }
    }
}
