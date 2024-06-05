package github.com.jbabe.repository.galleryImg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryImgJpa extends JpaRepository<GalleryImg, Integer> {
    @Query("SELECT g.fileUrl FROM GalleryImg g WHERE g.fileUrl IS NOT NULL")
    List<String> findAllFilePath();

    List<GalleryImg> findAllByGalleryGalleryId(int galleryId);
}
