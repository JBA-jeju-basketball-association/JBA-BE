package github.com.jbabe.service.post;

import github.com.jbabe.repository.post.Post;
import github.com.jbabe.repository.post.PostJpa;
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
    public Map<String, Object> getAllPostsList(Pageable pageable, String category) {
        Post.Category categoryEnum = Post.Category.pathToEnum(category);
        List<Post> notices = postJpa
                .findByIsAnnouncementTrueAndPostStatusAndCategory(Post.PostStatus.NORMAL, categoryEnum);
        Page<Post> posts = postJpa
                .findByIsAnnouncementFalseAndPostStatusAndCategory(pageable, Post.PostStatus.NORMAL, categoryEnum);
        if(pageable.getPageNumber()+1>posts.getTotalPages()) throw new NotFoundException("Page Not Found", pageable.getPageNumber());
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
    public PostResponseDto getPostByPostId(String category, Integer postId) {

        Post post = postJpa.findByIdUrlsJoin(Post.Category.pathToEnum(category), postId).orElseThrow(
                ()-> new NotFoundException("Post Not Found", postId));
        post.increaseViewCount();
        return PostMapper.INSTANCE.PostToPostResponseDto(post);
    }
}
