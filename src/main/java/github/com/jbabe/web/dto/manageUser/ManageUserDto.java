package github.com.jbabe.web.dto.manageUser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManageUserDto {
    private Integer userId;
    private String name;
    private String team;
    private String permission;
    private String email;
    private String phoneNum;
    private String userStatus;
    private String loginAt;
    private String createAt;
    private String updateAt;
    private String deleteAt;
    private String lockAt;
    private Integer failureCount;


//    private String dateOfBirth;
//    private String gender;
}
