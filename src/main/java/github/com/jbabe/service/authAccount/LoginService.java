package github.com.jbabe.service.authAccount;

import github.com.jbabe.config.security.JwtTokenConfig;
import github.com.jbabe.repository.redis.RedisTokenRepository;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.*;
import github.com.jbabe.web.dto.authAccount.AccessAndRefreshToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.http.ResponseCookie;
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

    @Transactional
    public AccessAndRefreshToken refreshToken(String requestAccessToken, String requestRefreshToken) {

        try {
            String redisRefreshToken = redisUtil.getData(requestAccessToken);
            if (!redisRefreshToken.equals(requestRefreshToken)) throw new ExpiredJwtException("RefreshToken 인증 오류");

            if (jwtTokenConfig.refreshTokenValidate(redisRefreshToken)) {
                Date refreshTokenExpiredTime = jwtTokenConfig.getTokenValidity(redisRefreshToken);
                long cookieExpiredTime = new Date(refreshTokenExpiredTime.getTime() - new Date().getTime()).getTime()/1000;

                String userEmail = jwtTokenConfig.getUserEmail(redisRefreshToken);

                String newAccessToken = jwtTokenConfig.createAccessToken(userEmail);
                String newRefreshToken = jwtTokenConfig.regenerateRefreshToken(userEmail, redisRefreshToken);
                ResponseCookie cookie = ResponseCookie.from("RefreshToken", newRefreshToken)
                        .maxAge(cookieExpiredTime) // 쿠키의 유효 시간
                        .path("/")  // 모든 페이지에서 사용가능
                        .secure(false) //TODO https 환경에서만 발동 여부 -> 배포시 true로 변경 필요
                        .sameSite("None") //TODO
                        // 동일 사이트와 크로스 사이트에 모두 쿠키 전송이 가능 -> 배포 시 변경필요
                        .httpOnly(true)  // 브라우저에서 쿠키에 접근할 수 없도록 제한
                        .build();

                jwtTokenConfig.saveRedisTokens(newAccessToken, newRefreshToken);
                redisUtil.deleteData(requestAccessToken);
                return new AccessAndRefreshToken(newAccessToken, cookie);
            }else{
                throw new ExpiredJwtException("refresh 토큰이 만료되었습니다.");
            }
        }catch (RedisSystemException | NullPointerException | ExpiredJwtException e) {
            throw new ExpiredJwtException("refresh 토큰이 만료되었습니다.");
        }
    }


    public AccessAndRefreshToken login(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String userEmail = authentication.getName();

            String accessToken = jwtTokenConfig.createAccessToken(userEmail);
            String refreshToken = jwtTokenConfig.createRefreshToken(userEmail);
            jwtTokenConfig.saveRedisTokens(accessToken, refreshToken); // redis에 Tokens 저장
            ResponseCookie cookie = ResponseCookie.from("RefreshToken", refreshToken)
                    .maxAge(Long.parseLong(jwtTokenConfig.getRefreshTokenValidMillisecond())/1000) // 쿠키의 유효 시간
                    .path("/")  // 모든 페이지에서 사용가능
                    .secure(false) //TODO https 환경에서만 발동 여부 -> 배포시 true로 변경 필요
                    .sameSite("None") //TODO 동일 사이트와 크로스 사이트에 모두 쿠키 전송이 가능 -> 배포 시 변경필요
                    .httpOnly(true)  // 브라우저에서 쿠키에 접근할 수 없도록 제한
                    .build();
            return new AccessAndRefreshToken(accessToken, cookie);

            //⬇️ 리스너 or 유저디테일서비스에서  날린 익셉션 그대로 던지기
        }catch (CustomBadCredentialsException | InternalAuthenticationServiceException e){
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotAcceptableException("로그인 할 수 없습니다.", email);
        }
    }

    @Transactional
    public ResponseCookie disableToken(String email, String accessToken) {
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

            return ResponseCookie.from("RefreshToken", "") // 클라이언트 refreshToken 삭제용 쿠키
                .maxAge(0)
                .build();
        }catch (Exception e){
            throw new BadRequestException(e.getMessage(), accessToken);
        }
    }
}
