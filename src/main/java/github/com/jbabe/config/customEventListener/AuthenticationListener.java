package github.com.jbabe.config.customEventListener;

import github.com.jbabe.config.security.AccountConfig;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.exception.CustomBadCredentialsException;
import github.com.jbabe.web.dto.authAccount.AuthFailureMessage;
import github.com.jbabe.web.dto.authAccount.FailureUserDto;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.net.http.HttpRequest;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthenticationListener {
    private final AccountConfig accountConfig;
    private final HttpServletRequest request;


    @EventListener
    public void handleBadCredentialsEvent(AuthenticationFailureBadCredentialsEvent event){
        accountConfig.failureCounting(event.getAuthentication().getName());
        FailureUserDto failureInfo = FailureUserDto.builder()
                .name(request.getSession().getAttribute("name").toString())
                .failureCount((Integer) request.getSession().getAttribute("failCount"))
                .status(User.UserStatus.valueOf(request.getSession().getAttribute("status").toString()))
                .failureDate((java.time.LocalDateTime) request.getSession().getAttribute("failureDate"))
                .withdrawalDate((java.time.LocalDateTime) request.getSession().getAttribute("withdrawalDate"))
                .build();


        throw new CustomBadCredentialsException(
                failureInfo.getStatus() == User.UserStatus.LOCKED?
                        event.getException().getMessage()+" 계정이 잠깁니다."
                        :event.getException().getMessage(),
                new AuthFailureMessage(failureInfo)

        );
    }
    @EventListener
    public void handleAuthSuccessEvent(AuthenticationSuccessEvent event){
        accountConfig.loginSuccessEvent(event.getAuthentication().getName());
    }
}
