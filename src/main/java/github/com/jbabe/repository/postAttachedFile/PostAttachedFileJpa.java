package github.com.jbabe.repository.postAttachedFile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostAttachedFileJpa extends JpaRepository<PostAttachedFile, Integer> {
}
