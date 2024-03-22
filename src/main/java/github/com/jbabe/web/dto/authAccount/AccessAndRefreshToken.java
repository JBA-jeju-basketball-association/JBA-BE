package github.com.jbabe.web.dto.authAccount;

import lombok.*;
import org.springframework.http.ResponseCookie;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessAndRefreshToken {
    private String accessToken;
    private ResponseCookie cookie;
}
