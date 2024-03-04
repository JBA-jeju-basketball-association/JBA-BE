package github.com.jbabe.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostJpa extends JpaRepository<Post, Integer> {
}
