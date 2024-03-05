package github.com.jbabe.service.authAccount;

import github.com.jbabe.repository.role.Role;
import github.com.jbabe.repository.role.RoleJpa;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.repository.userRole.UserRole;
import github.com.jbabe.repository.userRole.UserRoleJpa;
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
    private final RoleJpa roleJpa;
    private final UserRoleJpa userRoleJpa;

    @Transactional
    public String signUp(SignUpRequest signUpRequest) {
        if (userJpa.existsByEmail(signUpRequest.getEmail()))
            throw new ConflictException("이미 가입된 이메일입니다.", signUpRequest.getEmail());

        String pwd = passwordEncoder.encode(signUpRequest.getPassword());
        LocalDate dateOfBirth = LocalDate.of(signUpRequest.getYear(), signUpRequest.getMonth(), signUpRequest.getDay());
        Role role = roleJpa.findByName("ROLE_USER");

        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(pwd)
                .name(signUpRequest.getName())
                .phoneNum(signUpRequest.getPhoneNum())
                .gender(User.Gender.valueOf(signUpRequest.getGender()))
                .dateOfBirth(dateOfBirth)
                .userStatus(User.UserStatus.NORMAL)
                .failureCount(0)
                .createAt(LocalDateTime.now())
                .build();

        userJpa.save(user);
        userRoleJpa.save(
                UserRole.builder()
                        .user(user)
                        .role(role)
                        .build()
        );

        return signUpRequest.getName();
    }
}
