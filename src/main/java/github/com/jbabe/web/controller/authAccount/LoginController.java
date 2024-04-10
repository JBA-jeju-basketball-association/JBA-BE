package github.com.jbabe.web.controller.authAccount;

import github.com.jbabe.service.authAccount.LoginService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.authAccount.AccessAndRefreshToken;
import github.com.jbabe.web.dto.authAccount.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/sign")
@Slf4j
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseDto Login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        AccessAndRefreshToken accessAndRefreshToken = loginService.login(loginRequest.getEmail(), loginRequest.getPassword());
        httpServletResponse.setHeader("access-token", accessAndRefreshToken.getAccessToken());
        httpServletResponse.setHeader("refresh-token", accessAndRefreshToken.getRefreshToken());
        return new ResponseDto();
    }

    @PostMapping("/logout")
    public ResponseDto logout(@AuthenticationPrincipal CustomUserDetails customUserDetails, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        loginService.disableToken(customUserDetails.getUsername(), httpServletRequest.getHeader("AccessToken"));
        return new ResponseDto();
    }

    @PostMapping("/refresh-token")
    public ResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = request.getHeader("RefreshToken");
        String expiredAccessToken = request.getHeader("AccessToken");
        AccessAndRefreshToken newTokens = loginService.refreshToken(expiredAccessToken, refreshToken);

        response.setHeader("access-token", newTokens.getAccessToken());
        response.setHeader("refresh-token", newTokens.getRefreshToken());
        return new ResponseDto();
    }
}
