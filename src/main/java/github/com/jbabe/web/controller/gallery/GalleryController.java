package github.com.jbabe.web.controller.gallery;

import github.com.jbabe.service.gallery.GalleryService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.gallery.GalleryDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/api/gallery")
@RequiredArgsConstructor
public class GalleryController implements GalleryControllerDocs{
    private final GalleryService galleryService;

    @Override
    @GetMapping()//갤러리 목록조회
    public ResponseDto getGalleryList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam boolean official) {
        return new ResponseDto(galleryService.getGalleryList(PageRequest.of(page, size), official));
    }

    @Override
    @GetMapping("/{galleryId}")
    public ResponseDto getGalleryPost(
            @PathVariable int galleryId){
        return new ResponseDto(galleryService.getGalleryDetailsDto(galleryId));
    }


    @Override
    @PostMapping("/register")
    public ResponseDto regGalleryPost(@RequestBody GalleryDetailsDto requestRegister,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                      @RequestParam(name = "official", required = false) boolean isOfficial){
        //유저아이디 임시로 null일경우 5삽입
        Integer userId = Optional.ofNullable(customUserDetails)
                .map(CustomUserDetails::getUserId)
                .orElse(5);
        galleryService.registerGalleryPost(requestRegister,
                userId, isOfficial);
        return new ResponseDto();
    }


}
