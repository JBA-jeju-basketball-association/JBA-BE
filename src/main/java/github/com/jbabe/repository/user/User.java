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

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_num", nullable = false)
    private String phoneNum;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "user_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "team", nullable = false)
    private String team;

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
        NORMAL("normal"), HIDE("hide"), DELETE("delete"), LOCKED("locked");
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

    public static boolean isValidGender(String gender) {
        List<String> genders = new ArrayList<>();
        genders.add("MALE");
        genders.add("FEMALE");
        return genders.contains(gender);
    }

    public static boolean isValidBirthday(Integer year, Integer month, Integer day) {
        LocalDate dateOfBirth = LocalDate.of(year, month, day);
        LocalDate now = LocalDate.now();
        return dateOfBirth.isBefore(now);
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

    public User loginValueSetting(boolean failure){
        this.userStatus = failure ?
                (isFailureCountingOrLocking()||isUnlockTime() ? UserStatus.NORMAL : UserStatus.LOCKED)
                : UserStatus.NORMAL;
        this.failureCount = failure ?
                (isUnlockTime() ?
                        1
                        : (isFailureCountingOrLocking() ? failureCount + 1 : 0))
                : 0;
        this.lockAt = failure ? LocalDateTime.now() : null;
        return this;
    }

    public static LocalDate getBirthByLocalDate(String birth) {

        String dateStr = birth.substring(0, 6);
        LocalDate date;

        // 추출한 6자리 문자열을 yyMMdd 형식의 LocalDate로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        try {
            date = LocalDate.parse(dateStr, formatter);
        }catch (Exception e) {
            throw new InvalidReqeustException("주민번호 유효성 검사 실패", birth);
        }
        int currentYear = LocalDate.now().getYear();
        if (date.getYear() > currentYear) {
            date = date.withYear(date.getYear() - 100);
        }

        return date;
    }

    public static User.Gender transformGender(String birth) {
        String num = birth.substring(7, 8);
        if (num.equals("1") || num.equals("3")) {
            return User.Gender.MALE;
        }else if (num.equals("2") || num.equals("4")){
            return User.Gender.FEMALE;
        }else {
            throw new InvalidReqeustException("주민번호 유효성 검사 실패", birth);
        }
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

}



