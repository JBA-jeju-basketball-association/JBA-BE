package github.com.jbabe.web.controller.authAccount;

import github.com.jbabe.service.authAccount.LoginService;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.authAccount.LoginRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        String accessToken = loginService.login(loginRequest.getEmail(), loginRequest.getPassword());
        httpServletResponse.setHeader("AccessToken", accessToken);
        return new ResponseDto();
    }
}
