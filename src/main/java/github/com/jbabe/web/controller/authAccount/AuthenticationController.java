package github.com.jbabe.web.controller.authAccount;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.authAccount.LoginCookieService;
import github.com.jbabe.service.authAccount.SignService;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.ConflictException;
import github.com.jbabe.service.exception.ExpiredJwtException;
import github.com.jbabe.service.exception.InvalidReqeustException;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.authAccount.AccessAndRefreshToken;
import github.com.jbabe.web.dto.authAccount.LoginRequest;
import github.com.jbabe.web.dto.authAccount.SignUpRequest;
import github.com.jbabe.web.dto.authAccount.SocialLoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/auth")
@Slf4j
public class AuthenticationController {
    private final LoginCookieService loginCookieService;
    private final SignService signService;
    public final UserJpa userJpa;


    @PostMapping("/login")
    public ResponseDto LoginCookie(@RequestBody @Valid LoginRequest loginRequest) {
        AccessAndRefreshToken accessAndRefreshToken = loginCookieService.loginCookie(loginRequest.getEmail(), loginRequest.getPassword());
        return new ResponseDto(accessAndRefreshToken);
    }

    @PostMapping("/logout")
    public ResponseDto logoutCookie(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                    HttpServletRequest httpServletRequest) {
        String res = loginCookieService.disableTokenCookie(customUserDetails.getUsername(), httpServletRequest.getHeader("Authorization"));
        return new ResponseDto(res);
    }

    @PostMapping("/refresh-token")
    public ResponseDto refreshTokenCookie(HttpServletRequest request, HttpServletResponse response
    ) {
        String refreshToken = request.getHeader("RefreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) throw new ExpiredJwtException("쿠키에 리프레시 토큰이 없습니다.");
        String expiredAccessToken = request.getHeader("Authorization");
        if (expiredAccessToken == null || expiredAccessToken.isEmpty())
            throw new BadRequestException("Header에 AccessToken 이 없습니다.", "");

        AccessAndRefreshToken newTokens = loginCookieService.refreshTokenCookie(expiredAccessToken, refreshToken);
        return new ResponseDto(newTokens);
    }


    //회원가입
    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application.json",
                            examples = {
                                    @ExampleObject(name = "입력값 없음(모든 입력값 해당)", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"비어 있을 수 없습니다\",\n \"request\": \"email\"\n}"),
                                    @ExampleObject(name = "비밀번호 유효성 검사1", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"비밀번호는 영문과 특수문자 숫자를 포함하며 8자 이상 20자 이하여야 합니다.\",\n \"request\": \"password\"\n}"),
                                    @ExampleObject(name = "비밀번호 유효성 검사2", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"비밀번호에 특수문자는 !@#$^*+=-만 사용 가능합니다.\",\n \"request\": \"password\"\n}"),
                                    @ExampleObject(name = "휴대폰번호 유효성 검사", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"휴대폰번호 유효성 검사 실패\",\n \"request\": \"phoneNum\"\n}"),
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
        String name = signService.signUp(signUpRequest);
        return new ResponseDto(name);
    }

    @GetMapping("/check-email")
    public ResponseDto checkEmail(@RequestParam String email) {
        return new ResponseDto(signService.checkEmail(email));
    }

    @PostMapping("/social-login")
    public SocialLoginResponse socialLogin(@RequestParam(value = "socialId") String socialId,
                                           @RequestParam(value = "email", required = false) String email) {
        return loginCookieService.socialLogin(socialId, email);
    }

    @PostMapping("/social-sign-up")
    public SocialLoginResponse socialSignUp(@RequestParam(value = "socialId") String socialId,
                                            @RequestParam(value = "email") String email,
                                            @RequestParam(value = "name") String name,
                                            @RequestParam(value = "phoneNum") String phoneNum) {
        return loginCookieService.socialSignUp(socialId, email, name,phoneNum);
    }

    @PostMapping("/link-social")
    public ResponseDto linkEmailWithSocial(@RequestParam(value = "socialId") String socialId,
                                           @RequestParam(value = "email") String email) {
        return new ResponseDto(loginCookieService.linkEmailWithSocial(socialId, email));

    }

}
