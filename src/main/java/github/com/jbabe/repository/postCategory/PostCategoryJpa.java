package github.com.jbabe.repository.postCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCategoryJpa extends JpaRepository<PostCategory, Integer> {
}
