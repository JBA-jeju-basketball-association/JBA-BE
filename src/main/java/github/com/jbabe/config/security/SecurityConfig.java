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
                .authorizeRequests(a ->
                        a
                                .requestMatchers("/resource/static/**", "/v1/api/sign/sign-up", "/v1/api/sign/login",
                                        "v1/api/mail/*", "v1/api/gallery", "/v1/api/user/post/findEmail", "v1/api/user/post/checkUserInfo", "v1/api/user/update/password-in-findPassword").permitAll()
                                .requestMatchers(HttpMethod.POST, "/v1/api/post/*","/v1/api/gallery/register", "v1/api/competition/**").hasAnyRole("MASTER", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/v1/api/post/**", "/v1/api/gallery/*","v1/api/competition/**").hasAnyRole("MASTER", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/v1/api/post/*", "/v1/api/gallery/*", "v1/api/competition/**").hasAnyRole("MASTER", "ADMIN")
                                .requestMatchers("v1/api/competition/admin/**").hasAnyRole("MASTER", "ADMIN")
                                .requestMatchers("/test","v1/api/user/**").hasAnyRole("MASTER", "ADMIN", "REFEREE", "REFEREE_LEADER", "TABLE_OFFICIAL", "TABLE_OFFICIAL_LEADER", "USER") // 회원이면 가능

                                .requestMatchers("/v1/api/sign/logout").authenticated()


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
        corsConfiguration.setAllowedOrigins(List.of("https://localhost:3000", "http://localhost:3000", "https://jba.co.kr", swaggerConfig.getServerUrl() ));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addExposedHeader("access-token");
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
