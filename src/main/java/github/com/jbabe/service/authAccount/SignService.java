package github.com.jbabe.service.authAccount;


import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;

import github.com.jbabe.service.exception.ConflictException;
import github.com.jbabe.web.dto.authAccount.SignUpRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class SignService {
    private final UserJpa userJpa;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public String signUp(SignUpRequest signUpRequest) {


        String pwd = passwordEncoder.encode(signUpRequest.getPassword());
        LocalDate dateOfBirth = LocalDate.of(signUpRequest.getYear(), signUpRequest.getMonth(), signUpRequest.getDay());

        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(pwd)
                .name(signUpRequest.getName())
                .phoneNum(signUpRequest.getPhoneNum())
                .gender(User.Gender.valueOf(signUpRequest.getGender()))
                .dateOfBirth(dateOfBirth)
                .userStatus(User.UserStatus.NORMAL)
                .role(User.Role.ROLE_USER)
                .team(signUpRequest.getTeam())
                .failureCount(0)
                .createAt(LocalDateTime.now())
                .build();

        userJpa.save(user);

        return signUpRequest.getName();
    }
}
