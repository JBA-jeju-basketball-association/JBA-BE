package github.com.jbabe.web.controller.post;

import github.com.jbabe.config.JPAConfig;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.post.PostService;
import github.com.jbabe.service.storage.ServerDiskService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.myPage.MyPage;
import github.com.jbabe.web.dto.post.PostModifyDto;
import github.com.jbabe.web.dto.post.PostReqDto;
import github.com.jbabe.web.dto.post.PostsListDto;
import github.com.jbabe.web.dto.storage.FileDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/v1/api/post")
@RequiredArgsConstructor
public class PostController implements PostControllerDocs {
    private final PostService postService;
    private final ServerDiskService serverDiskService;
    private final JPAConfig jpaConfig;

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
            @PathVariable Integer postId) {
        return new ResponseDto(postService.getPostByPostId(category, postId));
    }

    @Override
    @PostMapping(value = "/{category}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto regPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String category,
            @RequestParam(required = false) boolean isOfficial,
            @RequestPart(value = "body") @Valid PostReqDto postReqDto,
            @RequestPart(value = "uploadFiles", required = false) List<MultipartFile> multipartFiles,
            @RequestParam(required = false) Optional<SaveFileType> type) {

        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            List<FileDto> files = serverDiskService.fileUploadAndGetUrl(multipartFiles);
            boolean response = postService.createPost(postReqDto, category, files, isOfficial, customUserDetails);
            if (response) return new ResponseDto();
        }
        boolean response = postService.createPost(postReqDto, category, null, isOfficial, customUserDetails);
        if (response) return new ResponseDto();
        else throw new BadRequestException("BRE", postReqDto);
    }

    @PostMapping(value = "/{category}/{postId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto updatePost(
//            @PathVariable String category,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Integer postId,
            @RequestParam(required = false) Boolean isOfficial,
            @RequestPart(value = "body") @Valid PostModifyDto postModifyDto,
            @RequestPart(value = "uploadFiles", required = false) List<MultipartFile> multipartFiles,
            @RequestParam(required = false) Optional<SaveFileType> type) {

        List<FileDto> files = null;
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            files = serverDiskService.fileUploadAndGetUrl(multipartFiles);
        }
        try {
            boolean response = postService.updatePost(postModifyDto, postId, files, isOfficial, customUserDetails);
            if (response) {
                return new ResponseDto();
            } else {
                throw new BadRequestException("BRE", postModifyDto);
            }
//        }catch (DataIntegrityViolationException sqlException){
//            throw new ConflictException("Title Duplication",postModifyDto.getTitle());
        } catch (JpaSystemException jpaSystemException) {
            throw new BadRequestException("Missing A Required Value", postModifyDto);
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseDto deletePost(@PathVariable int postId) {
        postService.deletePost(postId);
        return new ResponseDto();
    }

    @PutMapping("/{postId}/is-announcement")
    public ResponseDto updateIsAnnouncement(@PathVariable int postId) {
        postService.updateIsAnnouncement(postId);
        return new ResponseDto(HttpStatus.NO_CONTENT);
    }


}
