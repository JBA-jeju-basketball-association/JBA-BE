package github.com.jbabe.web.controller.post;

import github.com.jbabe.repository.post.Post;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.ConflictException;
import github.com.jbabe.service.post.PostService;
import github.com.jbabe.service.storage.StorageService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.SearchCriteriaEnum;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.myPage.MyPage;
import github.com.jbabe.web.dto.post.PostModifyDto;
import github.com.jbabe.web.dto.post.PostReqDto;
import github.com.jbabe.web.dto.post.PostsListDto;
import github.com.jbabe.web.dto.storage.FileDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/v1/api/post")
@RequiredArgsConstructor
public class PostController implements PostControllerDocs{
    private final PostService postService;
    private final StorageService storageService;

    @Override
    @GetMapping("/{category}")//게시물 목록 전체조회
    public ResponseDto getAllPostsList(@RequestParam(name = "page", defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(required = false) String keyword,
                                       @PathVariable String category) {
        MyPage<PostsListDto> contents = postService.searchPostList(
                        PageRequest.of(page, size, Sort.by(Sort.Order.desc("createAt")))
                        , category, keyword);

        return new ResponseDto(contents);

    }

    @Override
    @GetMapping("/{category}/{postId}")
    public ResponseDto getPostDetails(
            @PathVariable String category,
            @PathVariable Integer postId){
        return new ResponseDto(postService.getPostByPostId(category, postId));
    }

    @Override
    @PostMapping(value = "/{category}",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto regPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String category,
            @RequestParam(required = false) boolean isOfficial,
            @RequestPart (value = "body") @Valid PostReqDto postReqDto,
            @RequestPart(value = "uploadFiles", required = false) List<MultipartFile> multipartFiles,
            @RequestParam(required = false) Optional<SaveFileType> type){

        if(multipartFiles != null && !multipartFiles.isEmpty()){
            List<FileDto> files = storageService.fileUploadAndGetUrl(multipartFiles, type.orElseGet(()->SaveFileType.small));
            boolean response = postService.createPost(postReqDto, category, files, isOfficial);
            if(response) return new ResponseDto();
        }
        boolean response = postService.createPost(postReqDto, category, null, isOfficial);
        if(response) return new ResponseDto();
        else throw new BadRequestException("BRE", postReqDto);
    }

    @PutMapping(value = "/{category}/{postId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto updatePost(
//            @PathVariable String category,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Integer postId,
            @RequestParam(required = false) Boolean isOfficial,
            @RequestPart (value = "body") @Valid PostModifyDto postModifyDto,
            @RequestPart(value = "uploadFiles", required = false) List<MultipartFile> multipartFiles,
            @RequestParam(required = false) Optional<SaveFileType> type){

//        Integer userId = Optional.ofNullable(customUserDetails)
//                .map(CustomUserDetails::getUserId)
//                .orElse(5);
        List<FileDto> files = null;
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            files = storageService.fileUploadAndGetUrl(multipartFiles, type.orElseGet(() -> SaveFileType.small));
        }
        try {
            boolean response = postService.updatePost(postModifyDto, postId, files, isOfficial, customUserDetails);
            if (response) {
                return new ResponseDto();
            } else {
                throw new BadRequestException("BRE", postModifyDto);
            }
        }catch (DataIntegrityViolationException sqlException){
            throw new ConflictException("Title Duplication",postModifyDto.getTitle());
        }catch (JpaSystemException jpaSystemException){
            throw new BadRequestException("Missing A Required Value", postModifyDto);
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseDto deletePost(@PathVariable int postId){
        postService.deletePost(postId);
        return new ResponseDto();
    }

    @PutMapping("/{postId}/is-announcement")
    public ResponseDto updateIsAnnouncement(@PathVariable int postId){
        postService.updateIsAnnouncement(postId);
        return new ResponseDto(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/manage")
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

        return new ResponseDto(postService.getManagePostsList(pageable, keyword, searchCriteria, categoryEnum, startDate, endDate));
    }

}
