package github.com.jbabe.repository.gallery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryJpa extends JpaRepository<Gallery, Integer> {

    Page<Gallery> findByIsOfficialAndGalleryStatus(boolean isOfficial, Gallery.GalleryStatus galleryStatus, Pageable pageable);

}
