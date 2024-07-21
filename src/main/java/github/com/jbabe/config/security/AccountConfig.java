package github.com.jbabe.config.security;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
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
    private User findMyUser(String principal){
        return userJpa.findByEmail(principal).orElseThrow(()->
                new NotFoundException("Not Found User", principal));
    }

//    @Transactional
    public User failureCounting(String principal) {
        User failUser = userJpa.findByEmail(principal).orElseThrow(()->
                new NotFoundException("Not Found User", principal));
        userJpa.updateFailureCount(failUser.loginValueSetting(true));
        return failUser;


    }

    @Transactional
    public void loginSuccessEvent(String principal) {
        User sucUser = findMyUser(principal);
        sucUser.loginValueSetting(false);
    }
}
