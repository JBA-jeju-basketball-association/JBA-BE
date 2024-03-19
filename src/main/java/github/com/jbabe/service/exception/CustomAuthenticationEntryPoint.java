package github.com.jbabe.service.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.com.jbabe.config.security.JwtTokenConfig;
import github.com.jbabe.web.advice.errorResponseDto.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.warn(authException.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8");

        Object exception = request.getAttribute(JwtTokenConfig.EXCEPTION_HEADER_NAME);

        String[] responseStr = checkExceptionAndMakeMessage(request, exception);

        makeResponse(responseStr, response);

    }

    private String[] checkExceptionAndMakeMessage(HttpServletRequest request, Object exception) {

        String[] makeStr = new String[3];
        makeStr[0] = request.getHeader("AccessToken");
        makeStr[1] = exception==null?"Authentication failed":((RuntimeException) exception).getMessage();
        makeStr[2] = makeErrorMessage(exception);

        return makeStr;
    }

    private String makeErrorMessage(Object exception) {
        if (exception instanceof MalformedJwtException || exception instanceof UnsupportedJwtException || exception instanceof IllegalArgumentException) {
            return  "올바르지 않은 토큰";
        }else if(exception instanceof ExpiredJwtException){
            return  "만료된 토큰";
        } else if (exception instanceof SignatureException || exception instanceof NullPointerException) {
            return  "잘못된 서명의 토큰";
        } else if (exception instanceof RedisConnectionFailureException) {
            return  "redis 서버 에러";
        } else return "토큰 정보 없음";
    }

    private void makeResponse(String[] message, HttpServletResponse response) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), message[1], message[2], message[0]);
        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
    }
}
