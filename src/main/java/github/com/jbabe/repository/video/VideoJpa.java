package github.com.jbabe.repository.video;

import github.com.jbabe.web.dto.video.GetVideoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface VideoJpa extends JpaRepository<Video, Integer> {
    @Query("SELECT new github.com.jbabe.web.dto.video.GetVideoResponse(v.videoId, u.name, v.title, v.url, v.content, v.createAt) " +
            "FROM Video v " +
            "LEFT JOIN v.user u " +
            "WHERE v.videoStatus = :status AND v.isOfficial = :isOfficial AND v.title LIKE %:keyword% " +
            "ORDER BY v.videoId DESC ")
    Page<GetVideoResponse> findAllByVideoStatusAndIsOfficialWithKeyword(Video.VideoStatus status, boolean isOfficial, String keyword, Pageable pageable);

    boolean existsByTitle(String title);
}
