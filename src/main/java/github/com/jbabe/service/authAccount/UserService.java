package github.com.jbabe.service.authAccount;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.authAccount.UpdateAccountRequest;
import github.com.jbabe.web.dto.authAccount.UpdatePasswordRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserJpa userJpa;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public Object getUserInfo(CustomUserDetails customUserDetails) {
        Map<String, Object> userInfo = new HashMap<>();

        User user = userJpa.findById(customUserDetails.getUserId()).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다.", customUserDetails.getUserId()));
        String birth = user.getDateOfBirth().format(DateTimeFormatter.ofPattern("yyMMdd"));

        userInfo.put("name", user.getName());
        userInfo.put("role", User.getRoleKorean(user.getRole()));
        userInfo.put("email", user.getEmail());
        userInfo.put("phoneNum", user.getPhoneNum());
        userInfo.put("birth", birth);
        userInfo.put("team", user.getTeam());
        userInfo.put("gender", user.getGender());
        return userInfo;
    }

    @Transactional
    public String updateAccount(CustomUserDetails customUserDetails, UpdateAccountRequest request) {
        User user = userJpa.findById(customUserDetails.getUserId()).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다.", customUserDetails.getUserId()));
        String name = request.getName();
        String phoneNum = request.getPhoneNum();
        LocalDate birth = User.getBirthByLocalDate(request.getBirth());
        User.Gender gender = User.transformGender(request.getBirth());
        String team = request.getTeam();

        user.setName(name);
        user.setPhoneNum(phoneNum);
        user.setDateOfBirth(birth);
        user.setGender(gender);
        user.setTeam(team);
        user.setUpdateAt(LocalDateTime.now());
        userJpa.save(user);

        return "OK";
    }

    @Transactional
    public String deleteAccount(Integer userId) {
        User user = userJpa.findById(userId).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다.", userId));
        user.setUserStatus(User.UserStatus.DELETE);
        user.setDeleteAt(LocalDateTime.now());
        userJpa.save(user);

        return "OK";
    }


    @Transactional
    public String updatePassword(CustomUserDetails customUserDetails, UpdatePasswordRequest request) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(customUserDetails.getEmail(), request.getPrevPW()));
        User user = userJpa.findById(customUserDetails.getUserId()).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다.", customUserDetails.getUserId()));
        String pwd = passwordEncoder.encode(request.getNewPW());

        user.setPassword(pwd);
        user.setUpdateAt(LocalDateTime.now());
        userJpa.save(user);

        return "OK";
    }
}
