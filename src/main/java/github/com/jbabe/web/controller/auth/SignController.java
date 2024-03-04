package github.com.jbabe.web.controller.auth;

import github.com.jbabe.service.sign.SignService;
import github.com.jbabe.web.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/sign")
public class SignController {

    private final SignService signService;

    @PostMapping("/sign-up")
    public ResponseDto signUp(@RequestBody SignUpRequest signUpRequest) {

    }
}
