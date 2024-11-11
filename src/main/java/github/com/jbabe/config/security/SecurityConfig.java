package github.com.jbabe.config.security;

import github.com.jbabe.config.SwaggerConfig;
import github.com.jbabe.service.exception.CustomAuthenticationEntryPoint;
import github.com.jbabe.service.exception.CustomExceptionDeniedHandler;
import github.com.jbabe.web.filters.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenConfig jwtTokenConfig;
    private final SwaggerConfig swaggerConfig;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .csrf((c) -> c.disable())
                .httpBasic((h) -> h.disable())
                .formLogin(f -> f.disable())
                .rememberMe(r -> r.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(c-> c.configurationSource(corsConfig()))
                .authorizeHttpRequests(a ->
                        a
                                .requestMatchers("/v1/api/auth/logout").authenticated() // 로그인 상태에서만 가능

                                /*로그인 안된 상태에서만 가능,  /auth 경로의 check-email 은 get 요청이라 아래에서 permitAll 적용*/
                                .requestMatchers(HttpMethod.PUT, "/v1/api/account/password/reset").anonymous()
                                .requestMatchers(HttpMethod.POST, "/v1/api/auth/*", "/v1/api/account/**").anonymous()

                                .requestMatchers("/test","/v1/api/account/*", "/v1/api/competition/participate/*").authenticated() // 회원이면 가능

                                .requestMatchers("/v1/api/admin/**").hasAnyRole("MASTER", "ADMIN") // 겟요청 포함 관리자 기능
                                .requestMatchers(HttpMethod.GET, "/**" ).permitAll()//여기까지 설정 안된 겟 요청은 전부 허용
                                .requestMatchers( "/v1/api/video", "/v1/api/gallery/*", "/v1/api/post/**", "/v1/api/competition/**").hasAnyRole("MASTER", "ADMIN")//겟요청 제외 관리자 기능

                                .anyRequest().permitAll()// 기본값은 전체 허용


                )
                .exceptionHandling(e -> {
                    e.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
                    e.accessDeniedHandler(new CustomExceptionDeniedHandler());
                })
                .addFilterBefore(new JwtFilter(jwtTokenConfig), UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

    private CorsConfigurationSource corsConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000", "https://www.jba.co.kr", "https://jba.co.kr", "https://jejubasketball.shop", "https://www.jejubasketball.shop", swaggerConfig.getServerUrl() ,swaggerConfig.getNewServerUrl()));
        //요청에 인증정보를 같이 보내야 하는지
        corsConfiguration.setAllowCredentials(true);
        //클라이언트가 응답을 볼 수 잇는 헤더
        corsConfiguration.addExposedHeader("Authorization");
        corsConfiguration.addExposedHeader("RefreshToken");
        //클라이언트가 요청을 보낼 수 있는 헤더
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowedMethods(List.of("GET","PUT","POST","PATCH","DELETE","OPTIONS"));
        corsConfiguration.setMaxAge(1000L*60*60);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration);
        return source;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
