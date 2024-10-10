package github.com.jbabe.web.controller.community;

import github.com.jbabe.service.community.VideoService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.video.GetVideoResponse;
import github.com.jbabe.web.dto.video.PostVideoRequest;
import github.com.jbabe.web.dto.video.UpdateVideoRequest;
import io.swagger.v3.oas.annotations.Operation;
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

    @PostMapping
    @Operation(summary = "비디오 등록")
    public ResponseDto postVideo(@RequestBody @Valid PostVideoRequest request,
                                 @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String res = videoService.postVideo(request, customUserDetails);
        return new ResponseDto(res);
    }

    @GetMapping()
    @Operation(summary = "비디오 목록 불러오기")
    public ResponseDto getVideoList(@RequestParam(value = "isOfficial") boolean isOfficial,
                                    @RequestParam(value = "keyword") String keyword,
                                    Pageable pageable) {
        Page<GetVideoResponse> res = videoService.getVideoList(isOfficial, keyword, pageable);
        return new ResponseDto(res);
    }

    @GetMapping("/{videoId}")
    @Operation(summary = "비디오 아이디로 비디오 불러오기")
    public ResponseDto getVideo(@PathVariable Integer videoId) {
        GetVideoResponse res = videoService.getVideo(videoId);
        return new ResponseDto(res);
    }

    @PutMapping()
    @Operation(summary = "비디오 수정")
    public ResponseDto updateVideo(@RequestBody @Valid UpdateVideoRequest request) {
        String res = videoService.updateVideo(request);
        return new ResponseDto(res);
    }

    @DeleteMapping()
    @Operation(summary = "비디오 삭제")
    public ResponseDto deleteVideo(@RequestParam(value = "id") Integer id) {
        String res = videoService.deleteVideo(id);
        return new ResponseDto(res);
    }
}
