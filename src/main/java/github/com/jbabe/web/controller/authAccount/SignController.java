package github.com.jbabe.web.controller.authAccount;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.authAccount.SignService;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.InvalidReqeustException;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.authAccount.EmailRequest;
import github.com.jbabe.web.dto.authAccount.SignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/sign")
public class SignController {

    private final SignService signService;
    private final MailSender mailSender;

    @PostMapping("/sign-up")
    public ResponseDto signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        if (!User.isValidSpecialCharacterInPassword(signUpRequest.getPassword())) throw new InvalidReqeustException( "비밀번호에 특수문자는 !@#$^*+=-만 사용 가능합니다.", "");
        if (!User.isValidGender(signUpRequest.getGender())) throw new InvalidReqeustException("성별은 MALE 혹은 FEMALE 입니다.", signUpRequest.getGender());
        if (!signUpRequest.equalsPasswordAndPasswordConfirm()) throw new BadRequestException("비밀번호와 비밀번호 확인이 같지 않습니다.", "");

        String name = signService.signUp(signUpRequest);
        return new ResponseDto(name);
    }


}
