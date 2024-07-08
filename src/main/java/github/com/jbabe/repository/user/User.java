package github.com.jbabe.repository.user;

import github.com.jbabe.service.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Column(name = "phone_num", nullable = false, unique = true)
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
        MALE("mail"), FEMALE("femail");
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
    }

}



