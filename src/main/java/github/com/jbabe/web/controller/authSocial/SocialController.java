//package github.com.jbabe.web.controller.authSocial;
//
//
//import github.com.jbabe.service.social.SocialAuthenticationService;
//import github.com.jbabe.web.dto.ResponseDto;
//import github.com.jbabe.web.dto.authAccount.SignUpRequest;
//import github.com.jbabe.web.dto.authSocial.client.KakaoLoginParams;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/v1/api/sign/social")
//@RequiredArgsConstructor
//public class SocialController {
//    private final SocialAuthenticationService socialAuthenticationService;
//
//    @PostMapping("/kakao")
//    public ResponseDto loginKakao(@RequestBody KakaoLoginParams params, HttpServletResponse httpServletResponse) {
//
//        List<Object> tokenAndResponse = socialAuthenticationService.login(params);
//        httpServletResponse.setHeader("Token", (String) tokenAndResponse.get(0));
//        return (ResponseDto) tokenAndResponse.get(1);
//    }
//    @PostMapping("/connect")
//    public ResponseEntity<ResponseDto> connectAccount(
//            @RequestParam(name = "is-connect") boolean isConnect,
//            @RequestParam(name = "social-id") Long socialId){
//        return socialAuthenticationService.connectAccount(isConnect, socialId);
//    }
//    @PostMapping("/sign-up")
//    public ResponseEntity<ResponseDto> socialSignUp(
//            @RequestParam(name = "is-sign-up") boolean isSignUp,
//            @RequestParam(name = "social-id") Long socialId,
//            @RequestBody(required = false) SignUpRequest signUpRequest){
//        return socialAuthenticationService.socialSignUpFix(isSignUp, socialId, signUpRequest);
//    }
//}