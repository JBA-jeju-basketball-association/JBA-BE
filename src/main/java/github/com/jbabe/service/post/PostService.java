package github.com.jbabe.service.post;

import github.com.jbabe.repository.post.Post;
import github.com.jbabe.repository.post.PostJpa;
import github.com.jbabe.repository.postCategory.PostCategory;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.mapper.PostMapper;
import github.com.jbabe.web.dto.post.PostResponseDto;
import github.com.jbabe.web.dto.post.PostsListDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostJpa postJpa;
    public Map<String, Object> getAllPostsList(Pageable pageable) {

        List<Post> notices = postJpa.findByIsAnnouncementTrueAndPostStatus(Post.PostStatus.NORMAL);
        Page<Post> posts = postJpa.findByIsAnnouncementFalseAndPostStatus(pageable, Post.PostStatus.NORMAL);
        if(pageable.getPageSize()>posts.getTotalPages()) throw new NotFoundException("Page Not Found", pageable.getPageNumber());
        List<PostsListDto> postsListDto = new ArrayList<>();
        for(Post notice: notices) postsListDto.add(PostMapper.INSTANCE.PostToPostsListDto(notice));
        for(Post post: posts) postsListDto.add(PostMapper.INSTANCE.PostToPostsListDto(post));



        return Map.of(
                "posts", postsListDto,
                "totalPosts", posts.getTotalElements()+notices.size(),
                "totalPages", posts.getTotalPages()
        );
    }

    @Transactional
    public PostResponseDto getPostByPostId(PostCategory.Category category, Integer postId) {
        Post post = postJpa.findByIdUrlsJoin(postId).orElseThrow(()-> new NotFoundException("Post Not Found", postId));
        post.increaseViewCount();
        return PostMapper.INSTANCE.PostToPostResponseDto(post);
    }
}
