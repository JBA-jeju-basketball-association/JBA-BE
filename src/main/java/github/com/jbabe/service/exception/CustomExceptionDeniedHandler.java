package github.com.jbabe.service.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import github.com.jbabe.web.advice.errorResponseDto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class CustomExceptionDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8");

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), accessDeniedException.getMessage(),
                "접근 권한 없음", authorities.stream()
                .map(authority->authority.getAuthority()).collect(Collectors.joining(",")));

        String strResponse = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(errorResponse);
        response.getWriter().println(strResponse);

    }
}
