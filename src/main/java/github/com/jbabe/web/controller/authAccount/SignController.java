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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static github.com.jbabe.repository.user.User.isValidBirthday;

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
                                    @ExampleObject(name = "년도 유효성 검사(1900~2024)", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"1900 이상이어야 합니다 OR 2024 이하여야 합니다\",\n \"request\": \"year\"\n}"),
                                    @ExampleObject(name = "월 유효성 검사(1~12)", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"1 이상이어야 합니다 OR 12 이하여야 합니다\",\n \"request\": \"month\"\n}"),
                                    @ExampleObject(name = "일 유효성 검사(1~31)", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"1 이상이어야 합니다 OR 31 이하여야 합니다\",\n \"request\": \"day\"\n}"),
                                    @ExampleObject(name = "생년월일 유효성 검사", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"생년월일은 현재보다 이전이여야 합니다.\",\n \"request\": \"\"\n}"),
                                    @ExampleObject(name = "성별 유효성 검사", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"성별은 MALE 혹은 FEMALE 입니다.\",\n \"request\": \"남자\"\n}"),
                            }
                    )),
            @ApiResponse(responseCode = "409", description = "이미 가입된 정보",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "이미 가입된 이메일", value = "{\n  \"code\": 409,\n  \"message\": \"CONFLICT\",\n \"detailMessage\": \"이미 가입된 이메일입니다.\",\n \"request\": \"hansol@gmail.com\"\n}"),
                                    @ExampleObject(name = "이미 가입된 연락처", value = "{\n  \"code\": 409,\n  \"message\": \"CONFLICT\",\n \"detailMessage\": \"이미 해당 휴대폰 번호로 가입된 유저가 있습니다.\",\n \"request\": \"010-1111-2222\"\n}"),
                            }))


    })
    @PostMapping("/sign-up")
    public ResponseDto signUp(@RequestBody @Valid SignUpRequest signUpRequest) {

        if (!User.isValidSpecialCharacterInPassword(signUpRequest.getPassword()))
            throw new InvalidReqeustException("비밀번호에 특수문자는 !@#$^*+=-만 사용 가능합니다.", "password");
        if (!User.isValidGender(signUpRequest.getGender()))
            throw new InvalidReqeustException("성별은 MALE 혹은 FEMALE 입니다.", signUpRequest.getGender());
        if (!signUpRequest.equalsPasswordAndPasswordConfirm())
            throw new BadRequestException("비밀번호와 비밀번호 확인이 같지 않습니다.", "");
        if (!isValidBirthday(signUpRequest.getYear(), signUpRequest.getMonth(), signUpRequest.getDay()))
            throw new InvalidReqeustException("생년월일은 현재보다 이전이여야 합니다.", "");

        if (userJpa.existsByEmail(signUpRequest.getEmail()))
            throw new ConflictException("이미 가입된 이메일입니다.", signUpRequest.getEmail());
        if (userJpa.existsByPhoneNum(signUpRequest.getPhoneNum()))
            throw new ConflictException("이미 해당 휴대폰 번호로 가입된 유저가 있습니다.", signUpRequest.getPhoneNum());

        String name = signService.signUp(signUpRequest);
        return new ResponseDto(name);
    }


}
