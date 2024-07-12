package github.com.jbabe.service.admin;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.SearchQueryParamUtil;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.mapper.UserMapper;
import github.com.jbabe.web.dto.manageUser.ManageUserDto;
import github.com.jbabe.web.dto.manageUser.UserSearchCriteriaEnum;
import github.com.jbabe.web.dto.myPage.MyPage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserJpa userJpa;

    public MyPage<ManageUserDto> getUserInfo(UserSearchCriteriaEnum criteria, String keyword, User.Role permissions, Pageable pageable, LocalDate startDate, LocalDate endDate) {
        SearchQueryParamUtil.userValidateKeywordAndAdjustDates(keyword, criteria, startDate, endDate);
        startDate = startDate == null ? LocalDate.of(2024,1,1) : startDate;
        endDate = endDate == null ? LocalDate.now().plusDays(1) : endDate.plusDays(1);

        Page<User> users = userJpa.searchUser(criteria, keyword, permissions, pageable, startDate, endDate);
        if(!(pageable.getPageNumber() ==0) && pageable.getPageNumber()+1>users.getTotalPages()) throw new NotFoundException("Page Not Found", pageable.getPageNumber());

        return MyPage.<ManageUserDto>builder()
                .type(ManageUserDto.class)
                .content(users.stream()
                        .map(UserMapper.INSTANCE::UserToManageUserDto)
                        .toList())
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .build();
    }

    @Transactional
    public void updateUserPermission(int userId, User.Role permissions) {
        long execute = userJpa.updatePermissionAndGetExecute(userId, permissions);
        if(execute == 0) throw new NotFoundException("User not found", userId);

    }
}
