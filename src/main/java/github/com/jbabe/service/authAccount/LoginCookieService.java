package github.com.jbabe.service.authAccount;

import github.com.jbabe.config.security.JwtTokenConfig;
import github.com.jbabe.repository.redis.RedisTokenRepository;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.*;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.authAccount.AccessAndRefreshToken;
import github.com.jbabe.web.dto.authAccount.SocialLoginResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginCookieService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenConfig jwtTokenConfig;
    private final RedisUtil redisUtil;
    private final RedisTokenRepository redisTokenRepository;
    private final UserJpa userJpa;

    @Transactional
    public AccessAndRefreshToken refreshTokenCookie(String requestAccessToken, String requestRefreshToken) {

        try {
            String redisRefreshToken = redisUtil.getData(requestAccessToken);
            if (!redisRefreshToken.equals(requestRefreshToken)) throw new ExpiredJwtException("RefreshToken 인증 오류");
            if (jwtTokenConfig.refreshTokenValidate(redisRefreshToken)) {
                String userEmail = jwtTokenConfig.getUserEmail(redisRefreshToken);
                String newAccessToken = jwtTokenConfig.createAccessToken(userEmail);
                String newRefreshToken = jwtTokenConfig.regenerateRefreshToken(userEmail, requestRefreshToken);
                jwtTokenConfig.saveRedisTokens(newAccessToken, newRefreshToken);
                redisUtil.deleteData(requestAccessToken);
                return new AccessAndRefreshToken(newAccessToken, newRefreshToken);
            }else{
                throw new ExpiredJwtException("refresh 토큰이 만료되었습니다.");
            }
        }catch (RedisSystemException | NullPointerException | ExpiredJwtException e) {
            throw new ExpiredJwtException("refresh 토큰이 만료되었습니다.(레디스, 널 등)");
        }
    }




//    @Transactional
    public AccessAndRefreshToken loginCookie(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        try {
            String userEmail = authentication.getName();
            User user = userJpa.findByEmail(userEmail).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다.", userEmail));
            user.setLoginAt(LocalDateTime.now());
            userJpa.save(user);
            String accessToken = jwtTokenConfig.createAccessToken(userEmail);
            String refreshToken = jwtTokenConfig.createRefreshToken(userEmail);
            jwtTokenConfig.saveRedisTokens(accessToken, refreshToken); // redis에 Tokens 저장
            return new AccessAndRefreshToken(accessToken, refreshToken);

            //⬇️ 리스너 or 유저디테일서비스에서  날린 익셉션 그대로 던지기
        }catch (CustomBadCredentialsException | InternalAuthenticationServiceException e){
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotAcceptableException("로그인 할 수 없습니다.", email);
        }
    }



    @Transactional
    public String disableTokenCookie(String email, String accessToken) {
        try {
            String refreshToken = redisUtil.getData(accessToken);
            Date dataExp = jwtTokenConfig.getTokenValidity(accessToken);
        /*액세스토큰의 리프레시 토큰이 있는경우 리프레시 토큰과 액세스 토큰 둘다 해당 이메일의 토큰 무효화 리스트에 추가
        저장된 레디스 데이터의 유효시간은 리프레시토큰이 있는경우 액세스 토큰의 유효시간과 비교하여 더 긴 값으로 지정*/
            Set<String> tokens = new HashSet<>(Set.of(accessToken));
            if (refreshToken != null) {
                tokens.add(refreshToken);
                Date refreshTokenExpiration = jwtTokenConfig.getTokenValidity(refreshToken);
                dataExp = refreshTokenExpiration.after(dataExp) ? refreshTokenExpiration : dataExp;
            } else throw new BadRequestException("이미 로그아웃된 유저입니다.", accessToken);
            redisTokenRepository.addBlacklistToken(email, tokens,
                    Duration.between(Instant.now(), dataExp.toInstant()));

            return "OK";
        } catch (BadRequestException e) {
            throw new BadRequestException("이미 로그아웃된 유저입니다.", accessToken);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage(), accessToken);
        }
    }


    public SocialLoginResponse socialLogin(String socialId, String email) {
        User user;
        if (userJpa.findBySocialId(socialId).isPresent()) {
            user = userJpa.findBySocialId(socialId).get();
            String accessToken = jwtTokenConfig.createAccessToken(user.getEmail());
            String refreshToken = jwtTokenConfig.createRefreshToken(user.getEmail());
            jwtTokenConfig.saveRedisTokens(accessToken, refreshToken);
            user.setLoginAt(LocalDateTime.now());
            userJpa.save(user);
            return SocialLoginResponse.builder()
                    .status(200)
                    .message("소셜 로그인 성공")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } else if (userJpa.findByEmail(email).isPresent()){
            return SocialLoginResponse.builder()
                    .status(409)
                    .message(email)
                    .build();
        } else {
            return SocialLoginResponse.builder()
                    .status(404)
                    .message("해당 소셜 아이디로 가입된 유저가 없습니다.")
                    .build();
        }

    }

    public SocialLoginResponse socialSignUp(String socialId, String email, String name, String phoneNum) {
        if (userJpa.findByEmail(email).isPresent()) {
            return SocialLoginResponse.builder()
                    .status(409)
                    .message(email)
                    .build();
        } else {
            User user = User.builder()
                    .socialId(socialId)
                    .email(!Objects.equals(email, "null") ? email : "empty")
                    .name(name)
                    .phoneNum(!Objects.equals(phoneNum, "null") ? phoneNum : "010-0000-0000")
                    .userStatus(User.UserStatus.NORMAL)
                    .role(User.Role.ROLE_USER)
                    .failureCount(0)
                    .createAt(LocalDateTime.now())
                    .build();
            userJpa.save(user);
            String accessToken = jwtTokenConfig.createAccessToken(user.getEmail());
            String refreshToken = jwtTokenConfig.createRefreshToken(user.getEmail());
            jwtTokenConfig.saveRedisTokens(accessToken, refreshToken);
            user.setLoginAt(LocalDateTime.now());
            userJpa.save(user);
            return SocialLoginResponse.builder()
                    .status(200)
                    .message("소셜 회원가입 성공")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
    }


    @Transactional
    public String linkEmailWithSocial(String socialId, String email) {
        User user = userJpa.findByEmail(email).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다.", email));
        user.setSocialId(socialId);
        userJpa.save(user);
        return "OK";
    }
}
