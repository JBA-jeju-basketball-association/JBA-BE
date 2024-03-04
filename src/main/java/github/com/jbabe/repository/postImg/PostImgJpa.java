package github.com.jbabe.repository.postImg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImgJpa extends JpaRepository<PostImg, Integer> {
}
