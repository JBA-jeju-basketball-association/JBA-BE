package github.com.jbabe.repository.gallery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryJpa extends JpaRepository<Gallery, Integer> {
}
