package github.com.jbabe.repository.video;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoJpa extends JpaRepository<Video, Integer> {
    List<Video> findAllByVideoStatusAndIsOfficial(Video.VideoStatus status, boolean idOfficial);
}
