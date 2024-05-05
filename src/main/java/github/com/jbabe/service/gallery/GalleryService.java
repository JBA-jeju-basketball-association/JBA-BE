package github.com.jbabe.service.gallery;


import github.com.jbabe.repository.gallery.Gallery;
import github.com.jbabe.repository.gallery.GalleryJpa;
import github.com.jbabe.repository.galleryImg.GalleryImg;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.mapper.GalleryMapper;
import github.com.jbabe.web.dto.gallery.GalleryListDto;
import github.com.jbabe.web.dto.post.PostsListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GalleryService {
    private final GalleryJpa galleryJpa;
    public Object getGalleryList(Pageable pageable, boolean official) {
        Page<Gallery> galleryPages = galleryJpa
                .findByIsOfficialAndGalleryStatusJoin(official, Gallery.GalleryStatus.NORMAL, pageable);
        Page<Gallery> galleryPagess = galleryJpa
                .findAllByIsOfficialAndGalleryStatus(pageable, official, Gallery.GalleryStatus.NORMAL);
        Page<Gallery> all = galleryJpa.findAll(pageable);

        List<GalleryListDto> responseList = new ArrayList<>();
        if(pageable.getPageNumber()+1>galleryPages.getTotalPages()) throw new NotFoundException("Page Not Found", pageable.getPageNumber());

        for(Gallery gallery: galleryPages){
            GalleryImg firstImg = gallery.getGalleryImgs().get(0);

            GalleryListDto galleryListDto = GalleryMapper.INSTANCE
                    .GalleryToGalleryListDto(gallery, firstImg.getFileName(), firstImg.getImgUrl());
            responseList.add(galleryListDto);
        }
        return Map.of(
                "galleries", responseList,
                "totalGalleries", galleryPages.getTotalElements(),
                "totalPages", galleryPages.getTotalPages()
        );
    }


    @Transactional(readOnly = true)
    public Object getGalleryDetailsDto(int galleryId) {
        Gallery gallery = galleryJpa.findById(galleryId).orElseThrow(()->new NotFoundException("Not Found Gallery", galleryId));

        return GalleryMapper.INSTANCE.GalleryToGalleryDetailsDto(gallery);

    }
}
