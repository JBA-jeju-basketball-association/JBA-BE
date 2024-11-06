package github.com.jbabe.web.controller.authAccount;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.authAccount.LoginCookieService;
import github.com.jbabe.service.authAccount.UserService;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.InvalidReqeustException;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.authAccount.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/v1/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;
    private final LoginCookieService loginCookieService;
    @GetMapping()
    @Operation(summary = "로그인한 유저정보 조회")
    public ResponseDto getUserInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return new ResponseDto(userService.getUserInfo(customUserDetails));
    }

    @PutMapping("")
    @Operation(summary = "회원정보 수정")
    public ResponseDto updateAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                     @RequestBody @Valid UpdateAccountRequest request) {
        String response = userService.updateAccount(customUserDetails, request);
        return new ResponseDto(response);
    }

    @DeleteMapping()
    @Operation(summary = "회원탈퇴")
    public ResponseDto deleteAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Integer userId = customUserDetails.getUserId();
        String response = userService.deleteAccount(userId);
        return new ResponseDto(response);
    }

    @PutMapping("/password")
    @Operation(summary = "비밀번호 수정")
    public ResponseDto updatePassword(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                      @RequestBody UpdatePasswordRequest request,
                                      HttpServletRequest httpServletRequest,
                                      HttpServletResponse httpServletResponse) {
        if (!User.isValidSpecialCharacterInPassword(request.getNewPW()))
            throw new InvalidReqeustException("비밀번호에 특수문자는 !@#$^*+=-만 사용 가능합니다.", "password");
        if (!request.getNewPW().equals(request.getNewPWConfirm()))
            throw new BadRequestException("비밀번호와 비밀번호 확인이 같지 않습니다.", "");

        String response = userService.updatePassword(customUserDetails, request);
        return new ResponseDto(response);
    }


    @PostMapping("/verify/email")
    @Operation(summary = "이메일로 계정 정보 찾기")
    public ResponseDto findEmail(@RequestBody @Valid FindEmailRequest findEmailRequest) {
        String email = userService.findEmail(findEmailRequest);
        return new ResponseDto(email);
    }

    @PostMapping("/verify")
    @Operation(summary = "계정 정보(이메일이나 이름)로 계정 정보 찾기")
    public ResponseDto checkUserInfo(@RequestBody @Valid CheckUserInfoRequest request) {
        String res = userService.checkUserInfo(request);
        return new ResponseDto(res);
    }

    @PutMapping("/password/reset")
    @Operation(summary = "비밀번호 찾기 (비밀번호 다시 설정)")
    public ResponseDto updatePwInFindPassword(@RequestBody @Valid UpdatePasswordInFindPasswordRequest request) {
        if (!User.isValidSpecialCharacterInPassword(request.getPassword()))
            throw new InvalidReqeustException("비밀번호에 특수문자는 !@#$^*+=-만 사용 가능합니다.", "password");
        if (!Objects.equals(request.getPassword(), request.getConfirmPassword()))
            throw new BadRequestException("비밀번호와 비밀번호 확인이 같지 않습니다.", "");
        String res = userService.updatePwInFindPassword(request);
        return new ResponseDto(res);
    }


}
