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
    private ResponseCookie responseCookie;
    private String refreshToken;

    public AccessAndRefreshToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public AccessAndRefreshToken(String accessToken, ResponseCookie responseCookie) {
        this.accessToken = accessToken;
        this.responseCookie = responseCookie;
    }
}

