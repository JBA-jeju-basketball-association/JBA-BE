package github.com.jbabe.service.userDetails;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.CustomBadCredentialsException;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.web.dto.authAccount.AuthFailureMessage;
import github.com.jbabe.web.dto.authAccount.FailureUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class CustomUserDetailService implements UserDetailsService {

    private final UserJpa userJpa;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userJpa.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Not Found User", email));
        //⬇️인증 이전에 유저 상태 확인
        checkAccountStatusBeforeLogin(user);
        List<String> roles = Collections.singletonList(String.valueOf(user.getRole()));

        return CustomUserDetails.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(roles)
                .build();
    }

    public void checkAccountStatusBeforeLogin(User user){
        if(user.isLocked() && !user.isUnlockTime()){
            throw new CustomBadCredentialsException("Login Locked User", new AuthFailureMessage(new FailureUserDto(user)));
        } else if (user.isDisabled()) {
            throw  new CustomBadCredentialsException("Disable User", new AuthFailureMessage(new FailureUserDto(user)));
        }
    }

}
