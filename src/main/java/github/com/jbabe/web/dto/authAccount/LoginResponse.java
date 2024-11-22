package github.com.jbabe.web.dto.authAccount;

import github.com.jbabe.repository.user.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String email;
    private String name;
    private User.Role role;
}
