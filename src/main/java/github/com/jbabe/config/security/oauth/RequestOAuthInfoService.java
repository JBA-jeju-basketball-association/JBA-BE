package github.com.jbabe.config.security.oauth;

import github.com.jbabe.config.security.oauth.client.OAuthApiClient;
import github.com.jbabe.repository.oauth.OAuthProvider;
import github.com.jbabe.web.dto.authSocial.client.OAuthLoginParams;
import github.com.jbabe.web.dto.authSocial.server.OAuthInfoResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RequestOAuthInfoService {
    private final Map<OAuthProvider, OAuthApiClient> clients;

    public RequestOAuthInfoService(List<OAuthApiClient> clients) {
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthApiClient::oAuthProvider, Function.identity())
        );
    }

    public OAuthInfoResponse request(OAuthLoginParams params) {
        OAuthApiClient client = clients.get(params.oAuthProvider());

        String accessToken = client.requestAccessToken(params);

        return client.requestOauthInfo(accessToken);
    }
}
