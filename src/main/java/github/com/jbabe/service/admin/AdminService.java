package github.com.jbabe.service.admin;

import github.com.jbabe.repository.gallery.Gallery;
import github.com.jbabe.repository.gallery.GalleryJpaDao;
import github.com.jbabe.repository.post.Post;
import github.com.jbabe.repository.post.PostJpaDao;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.SearchQueryParamUtil;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.mapper.GalleryMapper;
import github.com.jbabe.service.mapper.PostMapper;
import github.com.jbabe.service.mapper.UserMapper;
import github.com.jbabe.web.dto.SearchCriteriaEnum;
import github.com.jbabe.web.dto.gallery.ManageGalleryDto;
import github.com.jbabe.web.dto.manageUser.ManageUserDto;
import github.com.jbabe.web.dto.manageUser.UserSearchCriteriaEnum;
import github.com.jbabe.web.dto.myPage.MyPage;
import github.com.jbabe.web.dto.post.ManagePostsDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserJpa userJpa;
    private final GalleryJpaDao galleryJpa;
    private final PostJpaDao postJpa;

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

    public MyPage<ManageGalleryDto> getManageGalleryList(Pageable pageable, Boolean official, String keyword, SearchCriteriaEnum searchCriteria, LocalDate startDate, LocalDate endDate){
        SearchQueryParamUtil.validateAndAdjustDates(keyword, searchCriteria, startDate, endDate);
        startDate = startDate == null ? LocalDate.of(2024,1,1) : startDate;
        endDate = endDate == null ? LocalDate.now().plusDays(1) : endDate.plusDays(1);

        Page<Gallery> galleries = galleryJpa.getGalleryManageList(pageable, official, keyword, searchCriteria, startDate, endDate);
        return makeResponseListAndToMyPageForManage(galleries, pageable);
    }

    private MyPage<ManageGalleryDto> makeResponseListAndToMyPageForManage(Page<Gallery> galleries, Pageable pageable) {
        if(pageable.getPageNumber()+1>galleries.getTotalPages()&&pageable.getPageNumber()!=0)
            throw new NotFoundException("Page Not Found", pageable.getPageNumber());
        List<ManageGalleryDto> responseList =  galleries.stream()
                .map(gallery -> GalleryMapper.INSTANCE
                        .GalleryToManageGalleryDto(gallery,
                                gallery.getGalleryImgs().isEmpty()?"https://www.irisoele.com/img/noimage.png":
                                        gallery.getGalleryImgs().get(0).getFileUrl()))
                .toList();

        return MyPage.<ManageGalleryDto>builder()
                .type(ManageGalleryDto.class)
                .content(responseList)
                .totalElements(galleries.getTotalElements())
                .totalPages(galleries.getTotalPages())
                .build();
    }


    //    @Transactional(readOnly = true)
    public MyPage<ManagePostsDto> getManagePostsList(Pageable pageable, String keyword, SearchCriteriaEnum searchCriteria, Post.Category categoryEnum, LocalDate startDate, LocalDate endDate) {
        SearchQueryParamUtil.validateAndAdjustDates(keyword, searchCriteria, startDate, endDate);
        startDate = startDate == null ? LocalDate.of(2024,1,1) : startDate;
        endDate = endDate == null ? LocalDate.now().plusDays(1) : endDate.plusDays(1);


        Page<Post> postList = postJpa.getPostsListFileFetch(pageable, keyword, searchCriteria, categoryEnum, startDate, endDate);
        if(!(pageable.getPageNumber() ==0) && pageable.getPageNumber()+1>postList.getTotalPages()) throw new NotFoundException("Page Not Found", pageable.getPageNumber());
        return MyPage.<ManagePostsDto>builder()
                .type(ManagePostsDto.class)
                .content(postList.stream()
                        .map(PostMapper.INSTANCE::PostToManagePostsDto)
                        .toList())
                .totalElements(postList.getTotalElements())
                .totalPages( postList.getTotalPages())
                .build();
    }
}
