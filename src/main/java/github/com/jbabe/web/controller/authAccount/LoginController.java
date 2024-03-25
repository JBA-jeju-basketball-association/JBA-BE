package github.com.jbabe.web.controller.authAccount;

import github.com.jbabe.config.security.JwtTokenConfig;
import github.com.jbabe.service.authAccount.LoginService;
import github.com.jbabe.service.exception.ExpiredJwtException;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.authAccount.AccessAndRefreshToken;
import github.com.jbabe.web.dto.authAccount.LoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/sign")
@Slf4j
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseDto Login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        AccessAndRefreshToken accessAndRefreshToken = loginService.login(loginRequest.getEmail(), loginRequest.getPassword());

        httpServletResponse.setHeader("AccessToken", accessAndRefreshToken.getAccessToken());
        httpServletResponse.setHeader("Set-Cookie", accessAndRefreshToken.getResponseCookie().toString());
        return new ResponseDto();
    }
    @PostMapping("/logout")
    public ResponseDto logout(@AuthenticationPrincipal CustomUserDetails customUserDetails, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        ResponseCookie cookie = loginService.disableToken(customUserDetails.getUsername(), httpServletRequest.getHeader("AccessToken"));
        httpServletResponse.setHeader("Set-Cookie", cookie.toString());
        return new ResponseDto();
    }

    @PostMapping("/refresh-token")
    public ResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response,
                                    @CookieValue(name = "RefreshToken", required = false) String refreshToken){

        if (refreshToken.isEmpty()) throw new ExpiredJwtException("쿠키에 리프레시 토큰이 없습니다.");
        String expiredAccessToken = request.getHeader("AccessToken");

        AccessAndRefreshToken newTokens = loginService.refreshToken(expiredAccessToken, refreshToken);

        response.setHeader("AccessToken", newTokens.getAccessToken());
        response.setHeader("Set-Cookie", newTokens.getResponseCookie().toString());

        return new ResponseDto();
    }
}
