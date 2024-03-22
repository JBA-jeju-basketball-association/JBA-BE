package github.com.jbabe.web.controller.authAccount;

import github.com.jbabe.config.security.JwtTokenConfig;
import github.com.jbabe.service.authAccount.LoginService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.authAccount.AccessAndRefreshToken;
import github.com.jbabe.web.dto.authAccount.ExpiredAccessToken;
import github.com.jbabe.web.dto.authAccount.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/sign")
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseDto Login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        AccessAndRefreshToken accessAndRefreshToken = loginService.login(loginRequest.getEmail(), loginRequest.getPassword());
        httpServletResponse.setHeader("AccessToken", accessAndRefreshToken.getAccessToken());
        httpServletResponse.setHeader("Set-Cookie", accessAndRefreshToken.getCookie().toString());
        return new ResponseDto();
    }
    @PostMapping("/logout")
    public ResponseDto logout(@AuthenticationPrincipal CustomUserDetails customUserDetails, HttpServletRequest httpServletRequest){
        loginService.disableToken(customUserDetails.getUsername(), httpServletRequest.getHeader("AccessToken"));
        return new ResponseDto();
    }

    @PostMapping("/refresh-token")
    public ResponseDto refreshToken(@RequestBody ExpiredAccessToken expiredAccessToken, HttpServletResponse httpServletResponse){
        String accessToken = loginService.refreshToken(expiredAccessToken.getExpiredAccessToken());
        httpServletResponse.setHeader("AccessToken", accessToken);
        return new ResponseDto();
    }
}
