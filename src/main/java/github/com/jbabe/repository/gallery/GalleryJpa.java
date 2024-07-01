package github.com.jbabe.repository.gallery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface GalleryJpa extends JpaRepository<Gallery, Integer>, QuerydslPredicateExecutor<Gallery>, GalleryRepositoryCustom {

    Page<Gallery> findByIsOfficialAndGalleryStatus(boolean isOfficial, Gallery.GalleryStatus galleryStatus, Pageable pageable);


}

