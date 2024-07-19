package github.com.jbabe.config.security;

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
                                .requestMatchers(HttpMethod.POST, "/v1/api/post/*","/v1/api/gallery/register").hasAnyRole("MASTER", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/v1/api/post/**", "/v1/api/gallery/*").hasAnyRole("MASTER", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/v1/api/post/*", "/v1/api/gallery/*").hasAnyRole("MASTER", "ADMIN")
                                .requestMatchers("/test","v1/api/competition/post/**", "v1/api/competition/delete/**", "v1/api/competition/update/**").hasAnyRole("MASTER", "ADMIN")
                                .requestMatchers("/v1/api/sign/logout").authenticated()

                                .requestMatchers("/resource/static/**", "/v1/api/sign/sign-up", "/v1/api/sign/login",
                                        "/mail/*", "v1/api/competition/competition", "v1/api/gallery").permitAll()
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
        corsConfiguration.setAllowedOrigins(List.of("*")); // TODO: 쿠키사용 시 변경
//        corsConfiguration.setAllowCredentials(true); // TODO: 쿠키사용 시 변경
        corsConfiguration.addExposedHeader("access-token");
        corsConfiguration.addExposedHeader("refresh-token");
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
