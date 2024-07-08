package github.com.jbabe.repository.gallery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface GalleryJpaDao extends JpaRepository<Gallery, Integer>, QuerydslPredicateExecutor<Gallery>, GalleryCustomDao {

    Page<Gallery> findByIsOfficialAndGalleryStatus(boolean isOfficial, Gallery.GalleryStatus galleryStatus, Pageable pageable);


}

