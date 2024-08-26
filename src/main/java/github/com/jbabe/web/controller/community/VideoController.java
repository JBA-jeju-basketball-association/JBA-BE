package github.com.jbabe.web.controller.community;

import github.com.jbabe.service.community.VideoService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.video.GetVideoResponse;
import github.com.jbabe.web.dto.video.PostVideoRequest;
import github.com.jbabe.web.dto.video.UpdateVideoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/api/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/post")
    public ResponseDto postVideo(@RequestBody @Valid PostVideoRequest request,
                                 @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String res = videoService.postVideo(request, customUserDetails);
        return new ResponseDto(res);
    }

    @GetMapping("/get/videoList")
    public ResponseDto getVideoList(@RequestParam(value = "isOfficial") boolean isOfficial,
                                    @RequestParam(value = "keyword") String keyword,
                                    Pageable pageable) {
        Page<GetVideoResponse> res = videoService.getVideoList(isOfficial, keyword, pageable);
        return new ResponseDto(res);
    }

    @GetMapping("/get")
    public ResponseDto getVideo(@RequestParam(value = "id") Integer id) {
        GetVideoResponse res = videoService.getVideo(id);
        return new ResponseDto(res);
    }

    @PutMapping("/update")
    public ResponseDto updateVideo(@RequestBody @Valid UpdateVideoRequest request) {
        String res = videoService.updateVideo(request);
        return new ResponseDto(res);
    }

    @DeleteMapping("/delete")
    public ResponseDto deleteVideo(@RequestParam(value = "id") Integer id) {
        String res = videoService.deleteVideo(id);
        return new ResponseDto(res);
    }
}
