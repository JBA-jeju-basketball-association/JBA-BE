package github.com.jbabe.repository.galleryImg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryImgJpa extends JpaRepository<GalleryImg, Integer> {
}
