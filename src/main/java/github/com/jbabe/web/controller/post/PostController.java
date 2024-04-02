package github.com.jbabe.web.controller.post;

import github.com.jbabe.service.post.PostService;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.post.PostsListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
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

}