package github.com.jbabe.web.controller.post;

import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.post.PostService;
import github.com.jbabe.service.storage.StorageService;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.post.PostReqDto;
import github.com.jbabe.web.dto.post.PostsListDto;
import github.com.jbabe.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final StorageService storageService;
    @GetMapping("/{category}")//게시물 목록 전체조회
    public ResponseDto getAllPostsList(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @PathVariable String category) {
        return new ResponseDto(postService.getAllPostsList(PageRequest.of(page, size), category));

    }

    @GetMapping("/{category}/{postId}")
    public ResponseDto getPostDetails(@PathVariable String category, @PathVariable Integer postId){
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
