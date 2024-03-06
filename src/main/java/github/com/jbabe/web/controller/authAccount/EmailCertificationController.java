package github.com.jbabe.web.controller.authAccount;

import github.com.jbabe.service.authAccount.EmailCertificationService;
import github.com.jbabe.service.authAccount.RedisUtil;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.web.dto.authAccount.EmailCheckRequest;
import github.com.jbabe.web.dto.authAccount.EmailRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/mail")
public class EmailCertificationController {

    private final MailSender mailSender;
    private final EmailCertificationService emailCertificationService;
    private final RedisUtil redisUtil;

    @PostMapping("/send-mail")
    public String sendMail(@RequestBody @Valid EmailRequest emailRequest) {
        System.out.println("이메일 인증 이메일 : " + emailRequest.getEmail());
        return emailCertificationService.joinEmail(emailRequest.getEmail());
    }

    @PostMapping("/check-auth-num")
    public String checkAuthNum(@RequestBody @Valid EmailCheckRequest emailCheckRequest) {
        Boolean checked = emailCertificationService.checkAuthNum(emailCheckRequest.getEmail(), emailCheckRequest.getAuthNum());
        if (checked) {
            return "OK";
        }else {
            throw new BadRequestException("잘못된 인증 번호 입니다.", emailCheckRequest.getAuthNum());
        }
    }

    @PostMapping("/redis-test")
    public String redisTest() {
        String email = "test";
        String password = "1234";
        String message;
        try {
            redisUtil.setDataExpire(password, email, 60 * 5L);
            message =  redisUtil.getData(password);
        }catch (Exception e) {
            e.printStackTrace();
            message = "fail";
        }
        return message;
    }
}
