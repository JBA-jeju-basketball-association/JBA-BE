//package github.com.jbabe.web.filters;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import github.com.jbabe.service.exception.ExpiredJwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//@Slf4j
//public class JwtExceptionFilter extends OncePerRequestFilter {
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        try {
//            filterChain.doFilter(request, response);
//        }catch (ExpiredJwtException e) {
//            log.error("expired token");
//            setErrorResponse(request, response, e);
//        }
//    }
//
//    public void setErrorResponse(HttpServletRequest request, HttpServletResponse response, Throwable ex) throws IOException{
//        response.setContentType("application/json; charset=UTF-8");
//        final Map<String, Object> body = new HashMap<>();
//        body.put("code", HttpServletResponse.SC_UNAUTHORIZED);
//        body.put("message", "ExpiredJwt");
//        body.put("detailMessage", "토큰이 만료되었습니다.");
//        final ObjectMapper mapper = new ObjectMapper();
//        mapper.writeValue(response.getOutputStream(), body);
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//    }
//}
