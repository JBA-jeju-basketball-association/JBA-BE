package github.com.jbabe.web.controller.admin;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.admin.AdminService;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.manageUser.UserSearchCriteriaEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/api/admin")
@RequiredArgsConstructor
public class AdminController implements AdminControllerDocs{
    private final AdminService adminService;

    @Override
    @GetMapping("/user")
    public ResponseDto getUserInfo(@RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String searchCriteriaString,
                                   @RequestParam(required = false) String permissionsStr,
                                   @RequestParam(required = false) LocalDate startDate,
                                   @RequestParam(required = false) LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);
        UserSearchCriteriaEnum criteria = keyword != null ? UserSearchCriteriaEnum.pathToEnum(searchCriteriaString) : null;
        User.Role permissions = permissionsStr != null ? User.Role.PathToRole(permissionsStr) : null;
        return new ResponseDto(adminService.getUserInfo(criteria, keyword, permissions, pageable, startDate, endDate));

    }
}
