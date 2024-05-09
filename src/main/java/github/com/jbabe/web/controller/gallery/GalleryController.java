package github.com.jbabe.web.controller.gallery;

import github.com.jbabe.service.gallery.GalleryService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.gallery.GalleryDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/gallery")
@RequiredArgsConstructor
public class GalleryController {
    private final GalleryService galleryService;

    @GetMapping()//갤러리 목록조회
    public ResponseDto getGalleryList(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "6") int size,
                                       @RequestParam boolean official) {

        return new ResponseDto(galleryService.getGalleryList(PageRequest.of(page, size), official));

    }

    @GetMapping("/{galleryId}")
    public ResponseDto getGalleryPost(@PathVariable int galleryId){
        return new ResponseDto(galleryService.getGalleryDetailsDto(galleryId));
    }

    @PostMapping("/register")
    public ResponseDto regGalleryPost(@RequestBody GalleryDetailsDto requestRegister,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                      @RequestParam(name = "official", required = false) boolean isOfficial){
        galleryService.registerGalleryPost(requestRegister, customUserDetails.getUserId(), isOfficial);
        return new ResponseDto();
    }


}
