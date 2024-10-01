package github.com.jbabe.service.authAccount;


import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;

import github.com.jbabe.service.exception.ConflictException;
import github.com.jbabe.web.dto.authAccount.SignUpRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SignService {
    private final UserJpa userJpa;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public String signUp(SignUpRequest signUpRequest) {

        String pwd = passwordEncoder.encode(signUpRequest.getPassword());
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(pwd)
                .name(signUpRequest.getName())
                .phoneNum(signUpRequest.getPhoneNum())
                .userStatus(User.UserStatus.NORMAL)
                .role(User.Role.ROLE_USER)
                .failureCount(0)
                .createAt(LocalDateTime.now())
                .build();

        userJpa.save(user);

        return signUpRequest.getName();
    }

    public String checkEmail(String email) {
        if (userJpa.existsByEmail(email)) throw new ConflictException("이미 가입된 이메일입니다.", email);
        return "OK";
    }
}
