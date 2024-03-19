package github.com.jbabe.web.dto.authAccount;

import com.fasterxml.jackson.annotation.JsonInclude;
import github.com.jbabe.repository.user.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AuthFailureMessage {
    private final String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Integer failureCount;
    private final String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final LocalDateTime failureDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final LocalDateTime withdrawalDate;

    public AuthFailureMessage(User myUser){
        this.name = myUser.getName();
        this.failureCount = myUser.isDisabled()||myUser.isLocked() ? null:myUser.getFailureCount();
        this.status = myUser.getUserStatus().name();
        this.failureDate = myUser.getLockAt();
        this.withdrawalDate = myUser.getDeleteAt();
    }
}