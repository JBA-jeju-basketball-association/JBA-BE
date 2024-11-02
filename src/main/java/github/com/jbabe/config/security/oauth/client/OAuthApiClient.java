package github.com.jbabe.config.security.oauth.client;


import github.com.jbabe.repository.oauth.OAuthProvider;
import github.com.jbabe.web.dto.authSocial.client.OAuthLoginParams;
import github.com.jbabe.web.dto.authSocial.server.OAuthInfoResponse;

public interface OAuthApiClient {
    OAuthProvider oAuthProvider();
    String requestAccessToken(OAuthLoginParams params);
    OAuthInfoResponse requestOauthInfo(String accessToken);
}
