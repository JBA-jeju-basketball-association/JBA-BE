package github.com.jbabe.web.controller.gallery;

import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.gallery.GalleryService;
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
        //유저아이디 임시로 null일경우 5삽입
        Integer userId = Optional.ofNullable(customUserDetails)
                .map(CustomUserDetails::getUserId)
                .orElse(5);
        galleryService.registerGalleryPost(requestRegister,
                userId, isOfficial);
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
    public ResponseDto modifyGalleryPost(@PathVariable int galleryId, @RequestBody @Valid GalleryDetailsDto requestModify,
                                         @RequestParam(name = "official", required = false) Boolean isOfficial){
        try {
            galleryService.modifyGalleryPost(galleryId, requestModify, isOfficial);
        }catch (DataIntegrityViolationException ex){
            throw new BadRequestException("SQLError", ex.getMessage());
        }
        return new ResponseDto();
    }

    @GetMapping("/manage")
    public ResponseDto getManageGalleryList(@RequestParam(name = "page", defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false)String searchCriteriaString,
                                            @RequestParam(required = false) Boolean official,
                                            @RequestParam(required = false) LocalDate startDate,
                                            @RequestParam(required = false) LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);
        SearchCriteriaEnum searchCriteria = keyword != null ? SearchCriteriaEnum.fromValue(searchCriteriaString) : null;

        return new ResponseDto(galleryService.getManageGalleryList(pageable, official, keyword, searchCriteria,startDate, endDate));
    }



}
