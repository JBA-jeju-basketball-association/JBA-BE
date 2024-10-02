package github.com.jbabe.web.dto.authSocial.client;

import github.com.jbabe.repository.oauth.OAuthProvider;
import org.springframework.util.MultiValueMap;

public interface OAuthLoginParams {
    OAuthProvider oAuthProvider();
    MultiValueMap<String, String> makeBody();
}
