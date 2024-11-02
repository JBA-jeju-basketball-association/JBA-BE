package github.com.jbabe.repository.user;

import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.InvalidReqeustException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "user")
@Getter
@Setter
@EqualsAndHashCode(of = "userId")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "social_id", unique = true)
    private String socialId;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_num", nullable = false)
    private String phoneNum;

    @Column(name = "user_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "failure_count", nullable = false)
    private Integer failureCount;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    @Column(name = "lock_at")
    private LocalDateTime lockAt;

    @Column(name = "login_at")
    private LocalDateTime loginAt;


    @Getter
    @AllArgsConstructor
    public enum Gender{
        MALE("male"), FEMALE("female");
        private final String path;
    }

    @Getter
    @AllArgsConstructor
    public enum UserStatus{
        NORMAL("normal"), HIDE("hide"), DELETE("delete"), LOCKED("locked"), TEMP("temp");
        private final String path;
    }

    @Getter
    @AllArgsConstructor
    public enum Role{
        ROLE_USER("user"), ROLE_MASTER("master"), ROLE_ADMIN("admin"), ROLE_REFEREE_LEADER("referee-leader"), ROLE_REFEREE("referee"), ROLE_TABLE_OFFICIAL_LEADER("table-official-leader"), ROLE_TABLE_OFFICIAL("table-official");
        final String path;
        public static Role PathToRole(String path){
            return Arrays.stream(Role.values())
                    .filter(role -> role.path.equals(path))
                    .findAny()
                    .orElseThrow(()->new BadRequestException("Unsupported permissions", path));
        }
    }


    public static boolean isValidSpecialCharacterInPassword(String pwd) {
        Pattern passPattern = Pattern.compile("\\W");
        Pattern passPattern2 = Pattern.compile("[!@#$%^*+=-]");
        int count = 0;

        for(int i = 0; i < pwd.length(); i++){
            String s = String.valueOf(pwd.charAt(i));
            Matcher passMatcher3 = passPattern.matcher(s);

            if(passMatcher3.find()){
                Matcher passMatcher4 = passPattern2.matcher(s);
                if(!passMatcher4.find()) count++;

            }
        }
        return count <= 0;
    }

    //login_extended
    public boolean isLocked(){
        return this.userStatus == UserStatus.LOCKED;
    }
    public boolean isDisabled(){
        return this.userStatus == UserStatus.HIDE || this.userStatus == UserStatus.DELETE;
    }
    public boolean isUnlockTime(){
        return this.lockAt != null
                && this.lockAt.isBefore(LocalDateTime.now().minusMinutes(5));
    }
    public boolean isFailureCountingOrLocking(){
        return this.failureCount < 4;
    }

    public void loginValueSetting(boolean failure){
        this.userStatus = failure ?
                (isFailureCountingOrLocking()||isUnlockTime() ? UserStatus.NORMAL : UserStatus.LOCKED)
                : UserStatus.NORMAL;
        this.failureCount = failure ?
                (isUnlockTime() ?
                        1
                        : (isFailureCountingOrLocking() ? failureCount + 1 : 0))
                : 0;
        this.lockAt = failure ? LocalDateTime.now() : null;
        this.loginAt = failure ? this.loginAt : LocalDateTime.now();
    }

    public static String getRoleKorean(User.Role role) {
        return switch (role) {
            case ROLE_MASTER -> "마스터";
            case ROLE_ADMIN -> "관리자";
            case ROLE_REFEREE -> "심판부";
            case ROLE_REFEREE_LEADER -> "심판이사";
            case ROLE_TABLE_OFFICIAL -> "경기부";
            case ROLE_TABLE_OFFICIAL_LEADER -> "경기이사";
            default -> "회원";
        };
    }

    public String emailMasking(String email) {
        int atIndex = email.indexOf("@");
        String modified = "";
        StringBuilder star = new StringBuilder();
        if (atIndex < 4) {
            modified = email;
        }else {
            star.append("*".repeat(Math.max(0, atIndex-3)));
            modified = email.substring(0,3) + star + email.substring(atIndex);
        }

        return modified;
    }

}



