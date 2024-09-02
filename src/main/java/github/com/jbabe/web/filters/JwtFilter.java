package github.com.jbabe.web.filters;

import github.com.jbabe.config.security.JwtTokenConfig;
import github.com.jbabe.service.exception.ExpiredJwtException;
import github.com.jbabe.service.exception.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenConfig jwtTokenConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = jwtTokenConfig.resolveToken(request);
        try {
            if (jwtToken != null && jwtTokenConfig.accessTokenValidate(jwtToken)) { // jwtToken 이 존재하고 유효하다면
                Authentication auth = jwtTokenConfig.getAuthentication(jwtToken); // jwtTokenConfig 에서 권한을 가져오고
                SecurityContextHolder.getContext().setAuthentication(auth); // SecurityContextHolder.getContext() 에 auth 를 넣어준다.
            }
        }/*
        catch (RedisConnectionFailureException e) {
            log.error(e.getMessage());
            throw new github.com.jbabe.service.exception.RedisConnectionFailureException("redis 서버 에러", "");
        } AuthenticationEntryPoint 로 옮김*/
        catch (Exception jwtException){//토큰 유효성 검사시 발생되는 익셉션들
            request.setAttribute(JwtTokenConfig.EXCEPTION_HEADER_NAME, jwtException);
        }

        filterChain.doFilter(request,response);
    }
    @Override//필터를 적용시키지 않을 url true 값이 배출되면 필터는 실행되지 않음
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return requestUri.startsWith("/resource/static")
                ||requestUri.equals("/v1/api/sign/sign-up")
                ||requestUri.equals("/v1/api/sign/login");
    }
}


