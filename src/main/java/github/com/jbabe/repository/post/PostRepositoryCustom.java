package github.com.jbabe.repository.post;

import github.com.jbabe.web.dto.SearchCriteriaEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PostRepositoryCustom {

    Page<Post> searchPostList(String keyword, Post.Category category, Pageable pageable);

    List<Post> getAnnouncementPosts(Post.Category categoryEnum, Sort sort);

    Page<Post> getPostsListFileFetch(Pageable pageable, String keyword, SearchCriteriaEnum searchCriteria, Post.Category categoryEnum, LocalDate startDate, LocalDate endDate);

    Optional<Post> getPostJoinFiles(Integer postId);



}
