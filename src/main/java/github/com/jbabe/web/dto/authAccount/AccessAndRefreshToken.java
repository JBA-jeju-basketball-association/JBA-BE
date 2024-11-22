package github.com.jbabe.web.dto.authAccount;

import jakarta.servlet.http.Cookie;
import lombok.*;
import org.springframework.http.ResponseCookie;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessAndRefreshToken {
    private String accessToken;
    private String refreshToken;
}

