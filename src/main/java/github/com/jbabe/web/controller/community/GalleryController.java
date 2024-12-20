package github.com.jbabe.web.controller.community;

import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.community.GalleryService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.SearchCriteriaEnum;
import github.com.jbabe.web.dto.gallery.GalleryDetailsDto;
import github.com.jbabe.web.dto.gallery.GalleryListDto;
import github.com.jbabe.web.dto.myPage.MyPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
            @RequestParam (required = false) String keyword,
            @RequestParam boolean official) {
        MyPage<GalleryListDto> responseData = keyword == null ?
                galleryService.getGalleryList(
                        PageRequest.of(page, size, Sort.by(Sort.Order.desc("createAt"))),
                        official):
                galleryService.searchGallery(page, size, official, keyword);

        return new ResponseDto(responseData);
    }

    @Override
    @GetMapping("/{galleryId}")
    public ResponseDto getGalleryPost(@PathVariable int galleryId){
        return new ResponseDto(galleryService.getGalleryDetailsDto(galleryId));
    }


    @Override
    @PostMapping("/register")
    public ResponseDto regGalleryPost(@RequestBody GalleryDetailsDto requestRegister,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                      @RequestParam(name = "official", required = false) boolean isOfficial){

        galleryService.registerGalleryPost(requestRegister,
                customUserDetails, isOfficial);
        return new ResponseDto();
    }
    @Override
    @DeleteMapping("/{galleryId}")
    public ResponseDto deleteGalleryPost(@PathVariable int galleryId){
        galleryService.deleteGalleryPost(galleryId);
        return new ResponseDto();
    }
    @Override
    @PutMapping("/{galleryId}")
    public ResponseDto modifyGalleryPost(@PathVariable int galleryId,
                                         @RequestBody @Valid GalleryDetailsDto requestModify,
                                         @RequestParam(name = "official", required = false) Boolean isOfficial){
        try {
            galleryService.modifyGalleryPost(galleryId, requestModify, isOfficial);
        }catch (DataIntegrityViolationException ex){
            throw new BadRequestException("SQLError", ex.getMessage());
        }
        return new ResponseDto();
    }





}
