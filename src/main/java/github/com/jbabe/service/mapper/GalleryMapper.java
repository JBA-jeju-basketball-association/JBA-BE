package github.com.jbabe.service.mapper;

import github.com.jbabe.repository.gallery.Gallery;
import github.com.jbabe.web.dto.gallery.GalleryDetailsDto;
import github.com.jbabe.web.dto.gallery.GalleryListDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GalleryMapper {
    GalleryMapper INSTANCE = Mappers.getMapper(GalleryMapper.class);


    @Mapping(target = "title", source = "gallery.name")
    GalleryListDto GalleryToGalleryListDto(Gallery gallery, String fileName, String imgUrl);

    @Mapping(target = "title", source = "gallery.name")
    @Mapping(target = "imgs", source = "gallery.galleryImgs")
    GalleryDetailsDto GalleryToGalleryDetailsDto(Gallery gallery);
}
