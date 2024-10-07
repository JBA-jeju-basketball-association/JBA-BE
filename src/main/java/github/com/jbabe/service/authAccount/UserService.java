package github.com.jbabe.service.authAccount;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.authAccount.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserJpa userJpa;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;

    public Object getUserInfo(CustomUserDetails customUserDetails) {
        User user = userJpa.findById(customUserDetails.getUserId()).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다.", customUserDetails.getUserId()));
        return UserInfoResponse.builder()
                .name(user.getName())
                .role(User.getRoleKorean(user.getRole()))
                .email(user.getEmail())
                .phoneNum(user.getPhoneNum())
                .isSocial(user.getSocialId() != null)
                .build();
    }

    @Transactional
    public String updateAccount(CustomUserDetails customUserDetails, UpdateAccountRequest request) {
        User user = userJpa.findById(customUserDetails.getUserId()).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다.", customUserDetails.getUserId()));
        String name = request.getName();
        String phoneNum = request.getPhoneNum();

        user.setName(name);
        user.setPhoneNum(phoneNum);
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

    public String findEmail(FindEmailRequest request) {
        User user = userJpa.findByNameAndPhoneNum(request.getName(), request.getPhoneNum()).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다.", ""));
        return user.emailMasking(user.getEmail());

    }

    public String checkUserInfo(CheckUserInfoRequest request) {
        boolean isCollect = userJpa.existsByEmailAndName(request.getEmail(), request.getName());
        if (!isCollect) throw new NotFoundException("유저를 찾을 수 없습니다.", "");
        return "OK";
    }

    @Transactional
    public String updatePwInFindPassword(UpdatePasswordInFindPasswordRequest request) {
        String serverAuthNum = redisUtil.getData(request.getEmail());
        if (serverAuthNum == null)
            throw new NotFoundException("인증번호가 만료되었습니다.", "");
        if (request.getCertificationNum() != Integer.parseInt(serverAuthNum)){
            throw new BadRequestException("인증번호가 맞지 않습니다.", "");
        }

        User user = userJpa.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException("user를 찾을 수 없습니다.", ""));

        String pwd = passwordEncoder.encode(request.getPassword());
        user.setPassword(pwd);
        userJpa.save(user);

        return "OK";
    }
}
