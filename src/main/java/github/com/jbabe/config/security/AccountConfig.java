package github.com.jbabe.config.security;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.web.dto.authAccount.FailureUserDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AccountConfig {
    private final UserJpa userJpa;
    private final HttpServletRequest request;


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

    @Transactional
    public void failureCounting(String principal) {
        User failUser = userJpa.findByEmail(principal).orElseThrow(()->
                new NotFoundException("Not Found User", principal));
        failUser.loginValueSetting(true);
//        userJpa.updateFailureCount(failUser.loginValueSetting(true));

//        return new FailureUserDto(failUser);

        //세션 활용 고민중
        HttpSession session = request.getSession();
        session.setAttribute("failCount", failUser.getFailureCount());
        session.setAttribute("name", failUser.getName()) ;
        session.setAttribute("status", failUser.getUserStatus().name());
        session.setAttribute("failureDate", failUser.getLockAt());
        session.setAttribute("withdrawalDate", failUser.getDeleteAt());


    }

    @Transactional
    public void loginSuccessEvent(String principal) {
        User sucUser = findMyUser(principal);
        sucUser.loginValueSetting(false);
    }
}
