package github.com.jbabe.web.dto.authSocial;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialAccountDto {
    private Long socialId;
    private String provider;
    private String nickName;
    private String email;
    private String imageUrl;
}
