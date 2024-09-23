package github.com.jbabe.web.controller.authAccount;

import github.com.jbabe.service.authAccount.LoginCookieService;
import github.com.jbabe.service.authAccount.LoginService;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.ExpiredJwtException;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.authAccount.AccessAndRefreshToken;
import github.com.jbabe.web.dto.authAccount.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/sign")
@Slf4j
public class LoginCookieController {
    private final LoginCookieService loginCookieService;

    @PostMapping("/login-cookie")
    public ResponseDto LoginCookie(@RequestBody @Valid LoginRequest loginRequest) {
        AccessAndRefreshToken accessAndRefreshToken = loginCookieService.loginCookie(loginRequest.getEmail(), loginRequest.getPassword());
        return new ResponseDto(accessAndRefreshToken);
    }

    @PostMapping("/logout-cookie")
    public ResponseDto logoutCookie(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                    HttpServletRequest httpServletRequest) {
        String res = loginCookieService.disableTokenCookie(customUserDetails.getUsername(), httpServletRequest.getHeader("Authorization"));
        return new ResponseDto(res);
    }

    @PostMapping("/refresh-token-cookie")
    public ResponseDto refreshTokenCookie(HttpServletRequest request, HttpServletResponse response
    ) {
        String refreshToken = request.getHeader("RefreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) throw new ExpiredJwtException("쿠키에 리프레시 토큰이 없습니다.");
        String expiredAccessToken = request.getHeader("Authorization");
        if (expiredAccessToken == null || expiredAccessToken.isEmpty())
            throw new BadRequestException("Header에 AccessToken 이 없습니다.", "");

        AccessAndRefreshToken newTokens = loginCookieService.refreshTokenCookie(expiredAccessToken, refreshToken);
        return new ResponseDto(newTokens);
    }

}
