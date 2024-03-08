package github.com.jbabe.repository.user;

import github.com.jbabe.repository.userRole.UserRole;
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

    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles;


    @Getter
    public enum Gender{
        MALE, FEMALE
    }

    @Getter
    public enum UserStatus{
        NORMAL, HIDE, DELETE
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
        List<Gender> genders = new ArrayList<>(Arrays.asList(Gender.values()));
        System.out.println(genders);
        return genders.contains(Gender.valueOf(gender));
    }
}



