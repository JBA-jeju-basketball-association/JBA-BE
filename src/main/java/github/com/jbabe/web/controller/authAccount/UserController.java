package github.com.jbabe.web.controller.authAccount;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.authAccount.LoginCookieService;
import github.com.jbabe.service.authAccount.UserService;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.InvalidReqeustException;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.authAccount.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/v1/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final LoginCookieService loginCookieService;

    @GetMapping("/get/user-info")
    public ResponseDto getUserInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return new ResponseDto(userService.getUserInfo(customUserDetails));
    }

    @PutMapping("/update")
    public ResponseDto updateAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                     @RequestBody @Valid UpdateAccountRequest request) {
        if (request.getBirth().length() != 8) {
            throw new InvalidReqeustException("주민번호 유효성 검사 실패", request.getBirth());
        }
        String response = userService.updateAccount(customUserDetails, request);
        return new ResponseDto(response);
    }

    @DeleteMapping("/delete")
    public ResponseDto deleteAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Integer userId = customUserDetails.getUserId();
        String response = userService.deleteAccount(userId);
        return new ResponseDto(response);
    }

    @PutMapping("/update/password")
    public ResponseDto updatePassword(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                      @RequestBody @Valid UpdatePasswordRequest request,
                                      HttpServletRequest httpServletRequest,
                                      HttpServletResponse httpServletResponse) {
        if (!User.isValidSpecialCharacterInPassword(request.getNewPW()))
            throw new InvalidReqeustException("비밀번호에 특수문자는 !@#$^*+=-만 사용 가능합니다.", "password");
        if (!request.getNewPW().equals(request.getNewPWConfirm()))
            throw new BadRequestException("비밀번호와 비밀번호 확인이 같지 않습니다.", "");

        String response = userService.updatePassword(customUserDetails, request);
        ResponseCookie cookie = loginCookieService.disableTokenCookie(customUserDetails.getUsername(), httpServletRequest.getHeader("Authorization"));
        httpServletResponse.setHeader("Set-Cookie", cookie.toString());
        return new ResponseDto(response);
    }


    @PostMapping("/post/findEmail")
    public ResponseDto findEmail(@RequestBody @Valid FindEmailRequest findEmailRequest) {
        String email = userService.findEmail(findEmailRequest);
        return new ResponseDto(email);
    }

    @PostMapping("post/checkUserInfo")
    public ResponseDto checkUserInfo(@RequestBody @Valid CheckUserInfoRequest request) {
        String res = userService.checkUserInfo(request);
        return new ResponseDto(res);
    }

    @PutMapping("update/password-in-findPassword")
    public ResponseDto updatePwInFindPassword(@RequestBody @Valid UpdatePasswordInFindPasswordRequest request) {
        if (!User.isValidSpecialCharacterInPassword(request.getPassword()))
            throw new InvalidReqeustException("비밀번호에 특수문자는 !@#$^*+=-만 사용 가능합니다.", "password");
        if (!Objects.equals(request.getPassword(), request.getConfirmPassword()))
            throw new BadRequestException("비밀번호와 비밀번호 확인이 같지 않습니다.", "");
        String res = userService.updatePwInFindPassword(request);
        return new ResponseDto(res);
    }
}
