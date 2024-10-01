package github.com.jbabe.service.community;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.repository.video.Video;
import github.com.jbabe.repository.video.VideoJpa;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.ConflictException;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.video.GetVideoResponse;
import github.com.jbabe.web.dto.video.PostVideoRequest;
import github.com.jbabe.web.dto.video.UpdateVideoRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final UserJpa userJpa;
    private final VideoJpa videoJpa;

    @Transactional
    public String postVideo(PostVideoRequest request, CustomUserDetails customUserDetails) {
        User user = userJpa.findByEmail(customUserDetails.getEmail()).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다,", ""));
        if (videoJpa.existsByTitle(request.getTitle()))
            throw new ConflictException("제목이 중복됩니다.", request.getTitle());
        Video newVideo = Video.builder()
                .user(user)
                .title(request.getTitle())
                .url(request.getUrl())
                .content(request.getContent())
                .isOfficial(request.getIsOfficial())
                .videoStatus(Video.VideoStatus.NORMAL)
                .createAt(LocalDateTime.now())
                .build();
        videoJpa.save(newVideo);
        return "OK";
    }

    public Page<GetVideoResponse> getVideoList(boolean isOfficial, String keyword, Pageable pageable) {
        return videoJpa.findAllByVideoStatusAndIsOfficialWithKeyword(Video.VideoStatus.NORMAL, isOfficial, keyword, pageable);
    }

    @Transactional
    public GetVideoResponse getVideo(Integer videoId) {
        Video video =  videoJpa.findById(videoId).orElseThrow(() -> new NotFoundException("게시물을 찾을 수 없습니다", videoId));
        return GetVideoResponse.builder()
                .videoId(video.getVideoId())
                .author(video.getUser().getName())
                .title(video.getTitle())
                .url(video.getUrl())
                .content(video.getContent())
                .build();
    }


    @Transactional
    public String updateVideo(UpdateVideoRequest request) {
        Video video = videoJpa.findById(request.getVideoId()).orElseThrow(() -> new NotFoundException("게시물을 찾을 수 없습니다,", request.getVideoId()));

        Video newVideo = Video.builder()
                .videoId(video.getVideoId())
                .user(video.getUser())
                .title(request.getTitle())
                .url(request.getUrl())
                .content(request.getContent())
                .isOfficial(video.getIsOfficial())
                .videoStatus(Video.VideoStatus.NORMAL)
                .createAt(video.getCreateAt())
                .updateAt(video.getUpdateAt())
                .build();
        videoJpa.save(newVideo);
        return "OK";
    }

    @Transactional
    public String deleteVideo(Integer id) {

        Video video = videoJpa.findById(id).orElseThrow(() -> new NotFoundException("게시물을 찾을 수 없습니다,", id));
        if (video.getVideoStatus() == Video.VideoStatus.DELETE) throw new BadRequestException("이미 삭제된 게시물입니다.", id);
        video.setVideoStatus(Video.VideoStatus.DELETE);
        videoJpa.save(video);
        return "OK";
    }
}
