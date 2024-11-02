package github.com.jbabe.web.controller.admin;

import github.com.jbabe.repository.post.Post;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.admin.AdminService;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.SearchCriteriaEnum;
import github.com.jbabe.web.dto.manageUser.UserSearchCriteriaEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

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

    @Override
    @PutMapping("/user/{userId}/permission")
    public ResponseDto updateUserPermission(@PathVariable int userId, @RequestParam String permissionsStr) {
        User.Role permissions = User.Role.PathToRole(permissionsStr);
        adminService.updateUserPermission(userId, permissions);
        return new ResponseDto();
    }
    @GetMapping("/gallery")
    public ResponseDto getManageGalleryList(@RequestParam(name = "page", defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false)String searchCriteriaString,
                                            @RequestParam(required = false) Boolean official,
                                            @RequestParam(required = false) LocalDate startDate,
                                            @RequestParam(required = false) LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);
        SearchCriteriaEnum searchCriteria = keyword != null ? SearchCriteriaEnum.fromValue(searchCriteriaString) : null;

        return new ResponseDto(adminService.getManageGalleryList(pageable, official, keyword, searchCriteria,startDate, endDate));
    }

    @GetMapping("/post")
    public ResponseDto getManagePostsList(@RequestParam(name = "page", defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size,
                                          @RequestParam(required = false) String keyword,
                                          @RequestParam(required = false)String searchCriteriaString,
                                          @RequestParam(required = false) String category,
                                          @RequestParam(required = false) LocalDate startDate,
                                          @RequestParam(required = false) LocalDate endDate) {

        Pageable pageable = PageRequest.of(page, size);
        SearchCriteriaEnum searchCriteria = keyword != null ? SearchCriteriaEnum.fromValue(searchCriteriaString) : null;
        Post.Category categoryEnum = category != null ? Post.Category.pathToEnum(category) : null;

        return new ResponseDto(adminService.getManagePostsList(pageable, keyword, searchCriteria, categoryEnum, startDate, endDate));
    }

}
