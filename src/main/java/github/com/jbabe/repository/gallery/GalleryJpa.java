package github.com.jbabe.repository.gallery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryJpa extends JpaRepository<Gallery, Integer> {
    @Query("SELECT g FROM Gallery g " +
            "JOIN FETCH g.galleryImgs gI " +
            "WHERE g.isOfficial = :isOfficial " +
            "AND g.galleryStatus = :galleryStatus"
    )
    Page<Gallery> findByIsOfficialAndGalleryStatusJoin(boolean isOfficial, Gallery.GalleryStatus galleryStatus, Pageable pageable);
    Page<Gallery> findAllByIsOfficialAndGalleryStatus(Pageable pageable, boolean isOfficial, Gallery.GalleryStatus galleryStatus);


}
