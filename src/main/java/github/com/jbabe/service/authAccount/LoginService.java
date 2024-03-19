package github.com.jbabe.service.authAccount;

import github.com.jbabe.config.security.JwtTokenConfig;
import github.com.jbabe.repository.redis.RedisTokenRepository;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenConfig jwtTokenConfig;
    private final RedisUtil redisUtil;
    private final RedisTokenRepository redisTokenRepository;
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
        }//토큰 validateToken 실패시 로직 작성되야됨
    }


    public String login(String email, String password) {

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = jwtTokenConfig.createAccessToken(email);
            String refreshToken = jwtTokenConfig.createRefreshToken(email);
            //saveRedisTokens 메서드에 Transactional 적용
            jwtTokenConfig.saveRedisTokens(accessToken, refreshToken); // redis에 Token 저장
            return accessToken;

            //⬇️ 리스너 or 유저디테일서비스에서  날린 익셉션 그대로 던지기
        }catch (CustomBadCredentialsException | InternalAuthenticationServiceException e){
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotAcceptableException("로그인 할 수 없습니다.", email);
        }
    }

    @Transactional
    public void disableToken(String email, String accessToken) {
        try{
        String refreshToken = redisUtil.getData(accessToken);
        Date dataExp = jwtTokenConfig.getTokenValidity(accessToken);
        /*액세스토큰의 리프레시 토큰이 있는경우 리프레시 토큰과 액세스 토큰 둘다 해당 이메일의 토큰 무효화 리스트에 추가
        저장된 레디스 데이터의 유효시간은 리프레시토큰이 있는경우 액세스 토큰의 유효시간과 비교하여 더 긴 값으로 지정*/
        Set<String> tokens = new HashSet<>(Set.of(accessToken));
        if(refreshToken!=null) {
            tokens.add(refreshToken);
            Date refreshTokenExpiration = jwtTokenConfig.getTokenValidity(refreshToken);
            dataExp = refreshTokenExpiration.after(dataExp) ? refreshTokenExpiration : dataExp;
        }
        redisTokenRepository.addBlacklistToken(email, tokens,
                Duration.between(Instant.now(), dataExp.toInstant()));
        }catch (Exception e){
            throw new BadRequestException(e.getMessage(), accessToken);
        }
    }
}
