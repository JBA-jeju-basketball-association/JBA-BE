package github.com.jbabe.service.gallery;


import github.com.jbabe.repository.gallery.Gallery;
import github.com.jbabe.repository.gallery.GalleryJpa;
import github.com.jbabe.repository.galleryImg.GalleryImg;
import github.com.jbabe.repository.galleryImg.GalleryImgJpa;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.mapper.GalleryMapper;
import github.com.jbabe.service.storage.StorageService;
import github.com.jbabe.web.dto.gallery.GalleryDetailsDto;
import github.com.jbabe.web.dto.gallery.GalleryListDto;
import github.com.jbabe.web.dto.post.PostsListDto;
import github.com.jbabe.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GalleryService {
    private final StorageService storageService;
    private final GalleryJpa galleryJpa;
    private final GalleryImgJpa galleryImgJpa;
    private final UserJpa userJpa;

    @Transactional(readOnly = true)
    public Object getGalleryList(Pageable pageable, boolean official) {
        Page<Gallery> galleryPages = galleryJpa
                .findByIsOfficialAndGalleryStatus(official, Gallery.GalleryStatus.NORMAL, pageable);

        List<GalleryListDto> responseList = new ArrayList<>();
        if(pageable.getPageNumber()+1>galleryPages.getTotalPages()) throw new NotFoundException("Page Not Found", pageable.getPageNumber());

        for(Gallery gallery: galleryPages){

            List<GalleryImg> galleryImgs = gallery.getGalleryImgs();
            GalleryImg firstImg = galleryImgs.isEmpty()?
                    GalleryImg.builder().fileName("갤러리 없는 갤러리 게시물").fileUrl("https://www.irisoele.com/img/noimage.png").build():
                    gallery.getGalleryImgs().get(0);

            GalleryListDto galleryListDto = GalleryMapper.INSTANCE
                    .GalleryToGalleryListDto(gallery, firstImg.getFileName(), firstImg.getFileUrl());
            responseList.add(galleryListDto);
        }
        return Map.of(
                "galleries", responseList,
                "totalGalleries", galleryPages.getTotalElements(),
                "totalPages", galleryPages.getTotalPages()
        );
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

        for(GalleryImg img: galleryEntity.getGalleryImgs()){
            img.setGallery(galleryEntity);
        }

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

}
