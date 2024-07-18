package github.com.jbabe.config.security;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AccountConfig {
    private final UserJpa userJpa;

    /* 계
     * 정
     * 관
     * 련
     * 로
     * 직
     * 모
     * 듈
     * 화
     */
    public User findMyUser(String principal){
        return userJpa.findByEmail(principal).orElseThrow(()->
                new NotFoundException("Not Found User", principal));
    }



//    @Transactional(noRollbackFor = BadCredentialsException.class)
    public void failureCounting(String principal) {
        User failUser = findMyUser(principal);
        failUser.loginValueSetting(true);

    }

    @Transactional
    public void loginSuccessEvent(String principal) {
        User sucUser = findMyUser(principal);
        sucUser.loginValueSetting(false);
    }
}
