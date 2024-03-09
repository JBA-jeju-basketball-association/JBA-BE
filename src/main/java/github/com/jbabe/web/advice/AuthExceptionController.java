package github.com.jbabe.web.advice;

import github.com.jbabe.service.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exceptions")
public class AuthExceptionController {
    @GetMapping(value = "/entrypoint")
    public void entrypointException() {
        throw new AccessDeniedException("로그인이 필요합니다", "");

    }

    @GetMapping(value = "/access-denied")
    public void accessDeniedException() {
        throw new AccessDeniedException("권한이 없습니다.", "");
    }
}
