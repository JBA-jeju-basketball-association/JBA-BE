package github.com.jbabe.web.controller.authAccount;

import github.com.jbabe.service.authAccount.LoginService;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.authAccount.LoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/login")
public class LoginController {
    private final LoginService loginService;

    public ResponseDto Login(@RequestBody @Valid LoginRequest loginRequest) {
        String message = loginService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return new ResponseDto();
    }
}
