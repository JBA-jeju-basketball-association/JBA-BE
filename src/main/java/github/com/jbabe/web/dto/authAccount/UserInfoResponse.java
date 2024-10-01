package github.com.jbabe.web.dto.authAccount;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoResponse {
    private String name;
    private String role;
    private String email;
    private String phoneNum;
}
