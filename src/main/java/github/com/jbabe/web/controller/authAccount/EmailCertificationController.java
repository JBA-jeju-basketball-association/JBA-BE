package github.com.jbabe.web.controller.authAccount;

import github.com.jbabe.service.authAccount.EmailCertificationService;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.authAccount.EmailCheckRequest;
import github.com.jbabe.web.dto.authAccount.EmailRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "회원가입 인증메일 보내기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "이메일 유효성 검사",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "이메일 입력 필요", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"이메일을 입력해주세요\",\n \"request\": \"email\"\n}"),
                                    @ExampleObject(name = "이메일 유효성 검사 실패", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"이메일 형식을 확인해주세요.\",\n \"request\": \"email\"\n}"),
                            }
                    )),
            @ApiResponse(responseCode = "409", description = "이미 가입된 이메일",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "이미 가입된 이메일", value = "{\n  \"code\": 409,\n  \"message\": \"CONFLICT\",\n \"detailMessage\": \"이미 가입된 이메일입니다.\",\n \"request\": \"hansol@gmail.com\"\n}")
                    ))
    })
    @PostMapping("/sign-up-send-mail")
    public ResponseDto signUpSendEmail(@RequestBody @Valid EmailRequest emailRequest) {

        System.out.println("이메일 인증 이메일 : " + emailRequest.getEmail());
        emailCertificationService.signUpSendEmail(emailRequest.getEmail());
        return new ResponseDto();
    }

    @Operation(summary = "인증번호 확인하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "유효성 검사",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "이메일 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"이메일을 입력해주세요.\",\n \"request\": \"email\"\n}"),
                                    @ExampleObject(name = "이메일 유효성 검사 실패", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"이메일 형식을 확인해주세요.\",\n \"request\": \"email\"\n}"),
                                    @ExampleObject(name = "인증번호 미입력", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"인증 번호를 입력해 주세요.\",\n \"request\": \"authNum\"\n}"),
                                    @ExampleObject(name = "인증번호 불일치", value = "{\n  \"code\": 400,\n  \"message\": \"BAD_REQUEST\",\n \"detailMessage\": \"잘못된 인증 번호 입니다.\",\n \"request\": \"123456\"\n}"),
                            }
                    ))
    })
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
