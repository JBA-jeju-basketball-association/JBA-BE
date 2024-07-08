package github.com.jbabe.service.gallery;


import github.com.jbabe.repository.gallery.Gallery;
import github.com.jbabe.repository.gallery.GalleryJpaDao;
import github.com.jbabe.repository.galleryImg.GalleryImg;
import github.com.jbabe.repository.galleryImg.GalleryImgJpa;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.SearchQueryParamUtil;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.mapper.GalleryMapper;
import github.com.jbabe.service.storage.StorageService;
import github.com.jbabe.web.dto.SearchCriteriaEnum;
import github.com.jbabe.web.dto.gallery.GalleryDetailsDto;
import github.com.jbabe.web.dto.gallery.GalleryListDto;
import github.com.jbabe.web.dto.gallery.ManageGalleryDto;
import github.com.jbabe.web.dto.myPage.MyPage;
import github.com.jbabe.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class GalleryService {
    private final StorageService storageService;
    private final GalleryJpaDao galleryJpa;
    private final GalleryImgJpa galleryImgJpa;
    private final UserJpa userJpa;



    @Transactional(readOnly = true)
    public MyPage<GalleryListDto> getGalleryList(Pageable pageable, boolean official) {
        Page<Gallery> galleryPages = galleryJpa.getGalleryList(pageable, official);

        return makeResponseListAndToMyPage(galleryPages, pageable);
    }

    private MyPage<GalleryListDto> makeResponseListAndToMyPage(Page<Gallery> galleryPages, Pageable pageable) {
        if(pageable.getPageNumber()+1>galleryPages.getTotalPages()&&pageable.getPageNumber()!=0)
            throw new NotFoundException("Page Not Found", pageable.getPageNumber());

        List<GalleryListDto> responseList =  galleryPages.stream()
                .map(gallery -> GalleryMapper.INSTANCE
                        .GalleryToGalleryListDto(gallery,
                                gallery.getGalleryImgs().isEmpty()?"갤러리 없는 갤러리 게시물":
                                        gallery.getGalleryImgs().get(0).getFileName(),
                                gallery.getGalleryImgs().isEmpty()?"https://www.irisoele.com/img/noimage.png":
                                        gallery.getGalleryImgs().get(0).getFileUrl()))
                .toList();
        return MyPage.<GalleryListDto>builder()
                .type(GalleryListDto.class)
                .content(responseList)
                .totalElements(galleryPages.getTotalElements())
                .totalPages(galleryPages.getTotalPages())
                .build();
    }


    @Transactional(readOnly = true)
    public GalleryDetailsDto getGalleryDetailsDto(int galleryId) {
        Gallery gallery = galleryJpa.findById(galleryId).orElseThrow(()->new NotFoundException("Not Found Gallery", galleryId));

        return GalleryMapper.INSTANCE.GalleryToGalleryDetailsDto(gallery);

    }

    @Transactional
    public void registerGalleryPost(GalleryDetailsDto requestRegister, Integer userId, boolean isOfficial) {

        Gallery galleryEntity = GalleryMapper.INSTANCE.GalleryDetailsDtoToGallery(requestRegister, userJpa.findById(userId)
                .orElseThrow(()->new NotFoundException("NotFoundUser", 5)), isOfficial);// 유저아이디 임시삽입

//        for(GalleryImg img: galleryEntity.getGalleryImgs()){
//            img.setGallery(galleryEntity);
//        }

        try{
            galleryJpa.save(galleryEntity);
        }catch (DataIntegrityViolationException exception) {
            throw new BadRequestException("SQLError", exception.getMessage());
        }

    }

    @Transactional
    public void deleteGalleryPost(int galleryId) {
        if (!galleryJpa.existsById(galleryId))
            throw new NotFoundException("Not Found Gallery", galleryId);

        deleteGalleryAssociatedData(galleryId);

        galleryJpa.deleteById(galleryId);

    }
    public void deleteGalleryAssociatedData(int galleryId){
        List<GalleryImg> imagesToDelete = galleryImgJpa.findAllByGalleryGalleryId(galleryId);
        if (!imagesToDelete.isEmpty()) storageService.uploadCancel(imagesToDelete.stream()
                .map(GalleryImg::getFileUrl).toList());
    }

    @Transactional
    public void modifyGalleryPost(int galleryId, GalleryDetailsDto requestModify, Boolean isOfficial){
        Gallery orginalGallery = galleryJpa.findById(galleryId).orElseThrow(
                ()-> new NotFoundException("Not Found Gallery", galleryId));
        List<GalleryImg> originalImgs = orginalGallery.getGalleryImgs();

        List<GalleryImg> imagesToBeErased = updateAndRemoveNonMatching(originalImgs, requestModify.getFiles());
        if (!imagesToBeErased.isEmpty()) {
            orginalGallery.getGalleryImgs().removeAll(imagesToBeErased);
            galleryImgJpa.deleteAll(imagesToBeErased);
        }

        List<GalleryImg> imageToBeAdded = forMissingFilesClickAdd(requestModify.getFiles(), orginalGallery);
        orginalGallery.notifyAndEditSubjectLineContent(requestModify, isOfficial, imageToBeAdded);

    }

    private List<GalleryImg> forMissingFilesClickAdd(List<FileDto> files, Gallery orginalGallery) {
        List<GalleryImg> imageToBeAdded = new ArrayList<>();
        for(FileDto file: files){
            if(orginalGallery.getGalleryImgs().stream()
                    .noneMatch(img -> img.getFileUrl().equals(file.getFileUrl()))
            ){
                GalleryImg img = GalleryImg.builder()
                        .fileName(file.getFileName())
                        .fileUrl(file.getFileUrl())
                        .gallery(orginalGallery)
                        .build();
                imageToBeAdded.add(img);
            }
        }
        return imageToBeAdded;
    }

    private List<GalleryImg> updateAndRemoveNonMatching(List<GalleryImg> originalImgs, List<FileDto> files) {
        List<GalleryImg> imagesToBeErased = new ArrayList<>();


        for(GalleryImg img: originalImgs){
            if(files.stream()
                    .noneMatch(fileDto -> fileDto.getFileUrl().equals(img.getFileUrl()))
            ){
                imagesToBeErased.add(img);
            }
        }
        if(!imagesToBeErased.isEmpty()) storageService.uploadCancel(imagesToBeErased.stream()
                .map(GalleryImg::getFileUrl).toList());
        return imagesToBeErased;
    }

//    @Transactional(readOnly = true)
    public MyPage<GalleryListDto> searchGallery(int page, int size, boolean official, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createAt")));
        Page<Gallery> galleryPages =  galleryJpa.searchGalleryList(official, keyword,pageable);

        return makeResponseListAndToMyPage(galleryPages, pageable);
    }

    public MyPage<ManageGalleryDto> getManageGalleryList(Pageable pageable, Boolean official, String keyword, SearchCriteriaEnum searchCriteria, LocalDate startDate, LocalDate endDate){
        SearchQueryParamUtil.validateAndAdjustDates(keyword, searchCriteria, startDate, endDate);
        startDate = startDate == null ? LocalDate.of(2024,1,1) : startDate;
        endDate = endDate == null ? LocalDate.now().plusDays(1) : endDate.plusDays(1);

        Page<Gallery> galleries = galleryJpa.getGalleryManageList(pageable, official, keyword, searchCriteria, startDate, endDate);
        return makeResponseListAndToMyPageForManage(galleries, pageable);
    }

    private MyPage<ManageGalleryDto> makeResponseListAndToMyPageForManage(Page<Gallery> galleries, Pageable pageable) {
        if(pageable.getPageNumber()+1>galleries.getTotalPages()&&pageable.getPageNumber()!=0)
            throw new NotFoundException("Page Not Found", pageable.getPageNumber());
        List<ManageGalleryDto> responseList =  galleries.stream()
                .map(gallery -> GalleryMapper.INSTANCE
                        .GalleryToManageGalleryDto(gallery,
                                gallery.getGalleryImgs().isEmpty()?"https://www.irisoele.com/img/noimage.png":
                                        gallery.getGalleryImgs().get(0).getFileUrl()))
                .toList();

        return MyPage.<ManageGalleryDto>builder()
                .type(ManageGalleryDto.class)
                .content(responseList)
                .totalElements(galleries.getTotalElements())
                .totalPages(galleries.getTotalPages())
                .build();
    }
}
