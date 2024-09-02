//package github.com.jbabe.web.controller.authAccount;
//
//import github.com.jbabe.service.authAccount.LoginService;
//import github.com.jbabe.service.exception.NotFoundException;
//import github.com.jbabe.service.userDetails.CustomUserDetails;
//import github.com.jbabe.web.dto.ResponseDto;
//import github.com.jbabe.web.dto.authAccount.AccessAndRefreshToken;
//import github.com.jbabe.web.dto.authAccount.LoginRequest;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.ExampleObject;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/v1/api/sign")
//@Slf4j
//public class LoginController {
//    private final LoginService loginService;
//
//    @Operation(summary = "로그인")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "400", description = "Invalid_Request",
//            content = @Content(mediaType = "application/json",
//            examples = {
//                    @ExampleObject(name = "이메일 유효성 검사 실패", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"이메일 형식이 올바르지 않습니다.\",\n \"request\": \"email\"\n}"),
//                    @ExampleObject(name = "이메일 빈값", value = "{\n  \"code\": 400,\n  \"message\": \"Invalid_Request\",\n \"detailMessage\": \"이메일을 입력해주세요.\",\n \"request\": \"email\"\n}"),
//            })),
//            @ApiResponse(responseCode = "401", description = "Unauthorized",
//                    content = @Content(mediaType = "application/json",
//                            examples = {
//                                    @ExampleObject(name = "틀린 비밀번호 입력", value = "{\n  \"code\": 401,\n  \"message\": \"UNAUTHORIZED\",\n \"detailMessage\": \"자격 증명에 실패하였습니다.\",\n \"request\": {\"name\": \"마스터\",\n  \"failureCount\": 1,\n  \"status\": \"NORMAL\",\n  \"failureDate\": \"2024-03-20T19:34:23.721394\"}\n}"),
//                                    @ExampleObject(name = "틀린 비밀번호 5회 입력", value = "{\n  \"code\": 401,\n  \"message\": \"UNAUTHORIZED\",\n \"detailMessage\": \"자격 증명에 실패하였습니다. 계정이 잠깁니다.\",\n \"request\": {\"name\": \"마스터\",\n  \"status\": \"LOCKED\",\n  \"failureDate\": \"2024-03-20T19:34:23.721394\"}\n}"),
//                                    @ExampleObject(name = "이미 잠긴 유저", value = "{\n  \"code\": 401,\n  \"message\": \"UNAUTHORIZED\",\n \"detailMessage\": \"Login Locked User\",\n \"request\": {\"name\": \"마스터\",\n  \"status\": \"LOCKED\",\n  \"failureDate\": \"2024-03-20T19:34:23.721394\"}\n}"),
//                            })),
//            @ApiResponse(responseCode = "404", description = "NOT_FOUND",
//                    content = @Content(mediaType = "application/json",
//                            examples = {
//                                    @ExampleObject(name = "해당 이메일로 가입된 유저 없음", value = "{\n  \"code\": 404,\n  \"message\": \"NOT_FOUND\",\n \"detailMessage\": \"Not Found User\",\n \"data\": \"hansol@gmail.com\"\n}"),
//                            }))
//    })
//    @PostMapping("/login")
//    public ResponseDto Login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
//        AccessAndRefreshToken accessAndRefreshToken = loginService.login(loginRequest.getEmail(), loginRequest.getPassword());
//        httpServletResponse.setHeader("access-token", accessAndRefreshToken.getAccessToken());
//        httpServletResponse.setHeader("refresh-token", accessAndRefreshToken.getRefreshToken());
//        return new ResponseDto();
//    }
//
//    @Operation(summary = "로그아웃")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "401", description = "토큰 인증 실패",
//                    content = @Content(mediaType = "application/json",
//                            examples = {
//                                    @ExampleObject(name = "만료된 토큰", value = "{\n  \"code\": 401,\n  \"message\": \"JWT expired at 2024-03-20T00:11:51Z. Current time: 2024-03-20T20:42:57Z, a difference of 73866076 milliseconds.  Allowed clock skew: 0 milliseconds.\",\n \"detailMessage\": \"만료된 토큰\",\n \"request\": \"asd.asd.asd\"\n}"),
//                                    @ExampleObject(name = "올바르지 않은 토큰", value = "{\n  \"code\": 401,\n  \"message\": \"Unable to read JSON value: {\\\\������!L��؉\",\n \"detailMessage\": \"올바르지 않은 토큰\",\n \"request\": \"asd.asd.asd\"\n}"),
//                                    @ExampleObject(name = "잘못된 서명의 토큰", value = "{\n  \"code\": 401,\n  \"message\": \"JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.\",\n \"detailMessage\": \"잘못된 서명의 토큰\",\n \"request\": \"asd.asd.asd\"\n}"),
//                                    @ExampleObject(name = "무효한 토큰", value = "{\n  \"code\": 401,\n  \"message\": \"Authentication failed\",\n \"detailMessage\": \"무효한 토큰\",\n \"request\": \"asd.asd.asd\"\n}"),
//                            })),
//            @ApiResponse(responseCode = "501", description = "Redis 에러",
//                    content = @Content(mediaType = "application/json",
//                            examples = {
//                                    @ExampleObject(name = "redis 서버 에러", value = "{\n  \"code\": 501,\n  \"message\": \"Unable to connect to Redis\",\n \"detailMessage\": \"redis 서버 에러\",\n \"request\": \"asd.asd.asd\"\n}"),
//                            }))
//    })
//    @PostMapping("/logout")
//    public ResponseDto logout(@AuthenticationPrincipal CustomUserDetails customUserDetails, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
//
//        loginService.disableToken(customUserDetails.getUsername(), httpServletRequest.getHeader("AccessToken"));
//        return new ResponseDto();
//    }
//
//    @Operation(summary = "리프레시 AccessToken")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "400", description = "Bad_Request",
//                    content = @Content(mediaType = "application/json",
//                            examples = @ExampleObject(name = "Header에 AccessToken 또는 RefreshToken이 없거나 비어있을 때", value = "{\n  \"code\": 400,\n  \"message\": \"BAD_REQUEST\",\n \"detailMessage\": \"Header에 토큰이 없습니다.\",\n \"request\": \"\"\n}")
//                    )),
//            @ApiResponse(responseCode = "401", description = "재로그인 필요",
//                    content = @Content(mediaType = "application/json",
//                            examples = @ExampleObject(name = "재로그인 필요", value = "{\n  \"code\": 401,\n  \"message\": \"ExpiredJwt\",\n \"detailMessage\": \"refresh 토큰이 만료되었습니다.\",\n \"request\": null\n}")
//                    ))
//    })
//    @PostMapping("/refresh-token")
//    public ResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
//
//        String refreshToken = request.getHeader("RefreshToken");
//        String expiredAccessToken = request.getHeader("AccessToken");
//        if (refreshToken == null || expiredAccessToken == null) {
//            throw new NotFoundException("Header에 토큰이 없습니다.", "");
//        }
//        AccessAndRefreshToken newTokens = loginService.refreshToken(expiredAccessToken, refreshToken);
//
//        response.setHeader("access-token", newTokens.getAccessToken());
//        response.setHeader("refresh-token", newTokens.getRefreshToken());
//        return new ResponseDto();
//    }
//}
