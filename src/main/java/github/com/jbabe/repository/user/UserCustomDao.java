package github.com.jbabe.repository.user;

import github.com.jbabe.web.dto.manageUser.UserSearchCriteriaEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface UserCustomDao {
    Page<User> searchUser(UserSearchCriteriaEnum criteria, String keyword, User.Role permissions, Pageable pageable, LocalDate startDate, LocalDate endDate);

}
