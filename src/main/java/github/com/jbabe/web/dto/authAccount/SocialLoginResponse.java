package github.com.jbabe.web.dto.authAccount;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLoginResponse {
    private Integer status;
    private String message;
    private String accessToken;
    private String refreshToken;
}
