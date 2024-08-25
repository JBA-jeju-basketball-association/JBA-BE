package github.com.jbabe.service.community;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.repository.video.Video;
import github.com.jbabe.repository.video.VideoJpa;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.video.GetVideoResponse;
import github.com.jbabe.web.dto.video.PostVideoRequest;
import github.com.jbabe.web.dto.video.UpdateVideoRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final UserJpa userJpa;
    private final VideoJpa videoJpa;

    @Transactional
    public String postVideo(PostVideoRequest request, CustomUserDetails customUserDetails) {
        User user = userJpa.findByEmail(customUserDetails.getEmail()).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다,", ""));

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

    public List<GetVideoResponse> getVideoList(boolean isOfficial) {
        List<Video> videos = videoJpa.findAllByVideoStatusAndIsOfficial(Video.VideoStatus.NORMAL, isOfficial);
        return videos.stream().map((video) ->
            GetVideoResponse.builder()
                    .videoId(video.getVideoId())
                    .author(video.getUser().getName())
                    .title(video.getTitle())
                    .url(video.getUrl())
                    .content(video.getContent())
                    .createAt(video.getCreateAt())
                    .build()
        ).toList();


    }

    @Transactional
    public String updateVideo(UpdateVideoRequest request) {
        Video video = videoJpa.findById(request.getVideoId()).orElseThrow(() -> new NotFoundException("게시물을 찾을 수 없습니다,", request.getVideoId()));

        Video newVideo = Video.builder()
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
