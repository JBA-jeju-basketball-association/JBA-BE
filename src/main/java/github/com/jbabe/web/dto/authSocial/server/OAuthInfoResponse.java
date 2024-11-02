package github.com.jbabe.web.dto.authSocial.server;


import github.com.jbabe.repository.oauth.OAuthProvider;

public interface OAuthInfoResponse {
    Long getSocialId();
    String getEmail();
    String getNickName();
    String getProfileImg();
    OAuthProvider getOAuthProvider();
}
