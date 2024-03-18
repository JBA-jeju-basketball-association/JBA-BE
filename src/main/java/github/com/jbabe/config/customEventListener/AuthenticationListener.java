package github.com.jbabe.config.customEventListener;

import github.com.jbabe.config.security.AccountConfig;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.exception.CustomBadCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthenticationListener {
    private final AccountConfig accountConfig;

    @EventListener
    public void handleBadCredentialsEvent(AuthenticationFailureBadCredentialsEvent event){
        User user = accountConfig.failureCounting(event.getAuthentication().getName());


        throw new CustomBadCredentialsException(user.getUserStatus() == User.UserStatus.HIDE?event.getException().getMessage()+" 계정이 잠깁니다."
                :event.getException().getMessage(), Map.of("count", user.getFailureCount(),
                "status", user.getUserStatus(),
                "lockAt", user.getLockAt()
        ));
    }
    @EventListener
    public void handleAuthSuccessEvent(AuthenticationSuccessEvent event){
        accountConfig.loginSuccessEvent(event.getAuthentication().getName());
    }
}
