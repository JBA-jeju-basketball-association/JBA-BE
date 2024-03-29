package github.com.jbabe.repository.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostJpa extends JpaRepository<Post, Integer> {
    Page<Post> findByIsAnnouncementFalseAndPostStatusAndCategory(Pageable pageable, Post.PostStatus postStatus, Post.Category category);
    List<Post> findByIsAnnouncementTrueAndPostStatusAndCategory(Post.PostStatus postStatus, Post.Category category);

    @Query(
            "SELECT p " +
                    "FROM Post p " +
                    "LEFT JOIN FETCH p.postAttachedFiles " +
                    "LEFT JOIN FETCH p.postImgs " +
                    "WHERE p.category = :category " +
                    "AND p.postId = :postId"
    )
    Optional<Post> findByIdUrlsJoin(Post.Category category, Integer postId);
}
