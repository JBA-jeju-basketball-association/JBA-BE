package github.com.jbabe.repository.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostJpa extends JpaRepository<Post, Integer> {
    Page<Post> findByIsAnnouncementFalseAndPostStatus(Pageable pageable, Post.PostStatus postStatus);
    List<Post> findByIsAnnouncementTrueAndPostStatus(Post.PostStatus postStatus);
}
