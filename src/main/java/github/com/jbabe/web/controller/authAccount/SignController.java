package github.com.jbabe.web.controller.authAccount;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.authAccount.SignService;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.ConflictException;
import github.com.jbabe.service.exception.InvalidReqeustException;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.authAccount.EmailRequest;
import github.com.jbabe.web.dto.authAccount.SignUpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/sign")
public class SignController {

    private final SignService signService;
    public final UserJpa userJpa;


    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application.json",
                            examples = {
                                    @ExampleObject(name = "입력값 없음(모든 입력값 해당)", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"비어 있을 수 없습니다\",\n \"request\": \"email\"\n}"),
                                    @ExampleObject(name = "비밀번호 유효성 검사1", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"비밀번호는 영문과 특수문자 숫자를 포함하며 8자 이상 20자 이하여야 합니다.\",\n \"request\": \"password\"\n}"),
                                    @ExampleObject(name = "비밀번호 유효성 검사2", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"비밀번호에 특수문자는 !@#$^*+=-만 사용 가능합니다.\",\n \"request\": \"password\"\n}"),
                                    @ExampleObject(name = "휴대폰번호 유효성 검사", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"휴대폰번호 유효성 검사 실패\",\n \"request\": \"phoneNum\"\n}"),
                                    @ExampleObject(name = "주민번호 유효성 검사 실패", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"주민번호 유효성 검사 실패\",\n \"request\": \"982739-1\"\n}"),
                            }
                    )),
            @ApiResponse(responseCode = "409", description = "이미 가입된 정보",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "이미 가입된 이메일", value = "{\n  \"code\": 409,\n  \"message\": \"CONFLICT\",\n \"detailMessage\": \"이미 가입된 이메일입니다.\",\n \"request\": \"hansol@gmail.com\"\n}"),
                            }))
    })
    @PostMapping("/sign-up")
    public ResponseDto signUp(@RequestBody @Valid SignUpRequest signUpRequest) {

        if (!User.isValidSpecialCharacterInPassword(signUpRequest.getPassword()))
            throw new InvalidReqeustException("비밀번호에 특수문자는 !@#$^*+=-만 사용 가능합니다.", "password");
        if (!signUpRequest.equalsPasswordAndPasswordConfirm())
            throw new BadRequestException("비밀번호와 비밀번호 확인이 같지 않습니다.", "");
        if (userJpa.existsByEmail(signUpRequest.getEmail()))
            throw new ConflictException("이미 가입된 이메일입니다.", signUpRequest.getEmail());
        if (signUpRequest.getBirth().length() != 8) {
            throw new InvalidReqeustException("주민번호 유효성 검사 실패", signUpRequest.getBirth());
        }
        String name = signService.signUp(signUpRequest);
        return new ResponseDto(name);
    }

    @GetMapping("/check-email")
    public ResponseDto checkEmail(@RequestParam String email) {
        return new ResponseDto(signService.checkEmail(email));
    }


}
