package github.com.jbabe.repository.postAttachedFile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostAttachedFileJpa extends JpaRepository<PostAttachedFile, Integer> {

    @Query("SELECT p.filePath FROM PostAttachedFile p WHERE p.filePath IS NOT NULL")
    List<String> findAllFilePath();

    List<PostAttachedFile> findAllByPostPostId(Integer postId);
}
