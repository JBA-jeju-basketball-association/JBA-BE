package github.com.jbabe.web.controller;

import github.com.jbabe.repository.redis.RedisTokenRepository;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class Test {

    private final UserJpa userJpa;
    private final RedisTokenRepository redisTokenRepository;

//    public Test(UserJpa userJpa) {
//        this.userJpa = userJpa;
//    }

    @GetMapping("/test")
    public String test(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = userJpa.findById(customUserDetails.getUserId()).orElseThrow(()-> new NotFoundException("Not Found User", ""));
        String userName = user.getName();
        return userName;
    }

    @PostMapping("/logoutTest")
    public String logoutTest(HttpServletRequest httpServletRequest){
        Set<String> oldToken = new HashSet<>();
        oldToken.add(httpServletRequest.getParameter("access"));
        oldToken.add(httpServletRequest.getParameter("refresh"));
        redisTokenRepository.addBlacklistToken(httpServletRequest.getParameter("email"), oldToken, Duration.ofMinutes(1));
        return "성공";
    }
}
