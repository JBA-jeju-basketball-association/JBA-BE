package github.com.jbabe.web.controller.post;

import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.post.PostService;
import github.com.jbabe.service.storage.StorageService;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.post.PostReqDto;

import github.com.jbabe.web.dto.storage.FileDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Tag(name = "Post", description = "게시물 관련 API")
@RestController
@RequestMapping("/v1/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @Operation(summary = "게시물 목록 조회", description = "각 카테고리별 게시물 목록을 공지게시글과 일반게시글을 반환")
    @ApiResponse(responseCode = "200",description = "게시물 목록 조회 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 페이지 (totalPages를 넘어가는 page로 요청한 경우)")
    @GetMapping("/{category}")//게시물 목록 전체조회
    public ResponseDto getAllPostsList(
            @Parameter(description = "페이지 쪽수 (기본값 = 0)")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "페이지당 공지를 제외한 일반게시글 갯수 (기본값 = 10)")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "카테고리 ex) notice, library, news", examples = {
                    @ExampleObject(name = "공지", value = "notice", description = "공지사항 카테고리"),
                    @ExampleObject(name = "뉴스", value = "news", description = "뉴스 카테고리"),
                    @ExampleObject(name = "라이브러리", value = "library", description = "자료실? 카테고리")})
            @PathVariable(name = "category") String category) {
        return new ResponseDto(postService.getAllPostsList(PageRequest.of(page, size), category));

    }

    private final StorageService storageService;

    @Operation(summary = "게시물 상세 조회", description = "원하는 게시물 조회")
    @ApiResponse(responseCode = "200",description = "게시물 조회 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 페이지 (해당 카테고리의 해당 게시물번호는 존재 하지않음)")
    @GetMapping("/{category}/{postId}")
    public ResponseDto getPostDetails(
            @Parameter(description = "카테고리 ex) notice, library, news", examples = {
                    @ExampleObject(name = "공지", value = "notice", description = "공지사항 카테고리"),
                    @ExampleObject(name = "뉴스", value = "news", description = "뉴스 카테고리"),
                    @ExampleObject(name = "라이브러리", value = "library", description = "자료실? 카테고리")})
            @PathVariable String category,
            @Parameter(description = "게시물 고유 번호", example = "5")
            @PathVariable Integer postId){
        return new ResponseDto(postService.getPostByPostId(category, postId));
    }


    @PostMapping("/{category}")
    public ResponseDto regPost(@PathVariable (required = false)String category,
                               @RequestPart (value = "body", required = false)PostReqDto postReqDto,
                               @RequestPart(value = "uploadFiles", required = false) List<MultipartFile> multipartFiles,
                               @RequestParam(required = false) Optional<SaveFileType> type){
        List<FileDto> files = storageService.fileUploadAndGetUrl(multipartFiles, type.orElseGet(()->SaveFileType.small));
        boolean response = postService.createPost(postReqDto, category, files);
        if(response) return new ResponseDto();
        else throw new BadRequestException("BRE", postReqDto);
    }

}
