package github.com.jbabe.web.dto.authAccount;

import github.com.jbabe.repository.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class FailureUserDto {
    private final String name;
    private final Integer failureCount;
    private final User.UserStatus status;
    private final LocalDateTime failureDate;
    private final LocalDateTime withdrawalDate;

    public FailureUserDto(User user){
        this.name = user.getName();
        this.failureCount = user.getFailureCount();
        this.status = user.getUserStatus();
        this.failureDate = user.getLockAt();
        this.withdrawalDate = user.getDeleteAt();
    }

    public boolean isLocked(){
        return this.status == User.UserStatus.LOCKED;
    }
    public boolean isDisabled(){
        return this.status == User.UserStatus.HIDE || this.status == User.UserStatus.DELETE;
    }
}
