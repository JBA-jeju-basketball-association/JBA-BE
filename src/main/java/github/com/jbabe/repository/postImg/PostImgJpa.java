package github.com.jbabe.repository.postImg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImgJpa extends JpaRepository<PostImg, Integer> {
    @Query("SELECT c.imgUrl FROM PostImg c WHERE c.imgUrl IS NOT NULL")
    List<String> findAllFilePath();
    List<PostImg> findAllByPostPostId(Integer postId);
}
