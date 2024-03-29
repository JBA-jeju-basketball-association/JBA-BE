package github.com.jbabe.web.controller.authAccount;

import github.com.jbabe.service.authAccount.EmailCertificationService;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.authAccount.EmailCheckRequest;
import github.com.jbabe.web.dto.authAccount.EmailRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/mail")
public class EmailCertificationController {

    private final EmailCertificationService emailCertificationService;

    @PostMapping("/sign-up-send-mail")
    public ResponseDto signUpSendEmail(@RequestBody @Valid EmailRequest emailRequest) {
        System.out.println("이메일 인증 이메일 : " + emailRequest.getEmail());
        emailCertificationService.signUpSendEmail(emailRequest.getEmail());
        return new ResponseDto();
    }

    @PostMapping("/check-auth-num")
    public ResponseDto checkAuthNum(@RequestBody @Valid EmailCheckRequest emailCheckRequest) {
        Boolean checked = emailCertificationService.checkAuthNum(emailCheckRequest.getEmail(), emailCheckRequest.getAuthNum());
        if (checked) {
            return new ResponseDto();
        }else {
            throw new BadRequestException("잘못된 인증 번호 입니다.", emailCheckRequest.getAuthNum());
        }
    }

}
