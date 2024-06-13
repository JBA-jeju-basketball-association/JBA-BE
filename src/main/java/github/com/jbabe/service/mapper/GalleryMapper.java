package github.com.jbabe.service.mapper;

import github.com.jbabe.repository.gallery.Gallery;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.web.dto.gallery.GalleryDetailsDto;
import github.com.jbabe.web.dto.gallery.GalleryListDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GalleryMapper {
    GalleryMapper INSTANCE = Mappers.getMapper(GalleryMapper.class);


    @Mapping(target = "title", source = "gallery.name")
    @Mapping(target = "createAt", dateFormat = "yyyy-MM-dd")
    GalleryListDto GalleryToGalleryListDto(Gallery gallery, String fileName, String imgUrl);

    @Mapping(target = "title", source = "gallery.name")
    @Mapping(target = "files", source = "gallery.galleryImgs")
    GalleryDetailsDto GalleryToGalleryDetailsDto(Gallery gallery);

    @Mapping(target = "galleryImgs", source = "galleryDetailsDto.files")
    @Mapping(target = "name", source = "galleryDetailsDto.title")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "isOfficial", source = "isOfficial")
    @Mapping(target = "createAt", ignore = true)
    Gallery GalleryDetailsDtoToGallery(GalleryDetailsDto galleryDetailsDto, User user, boolean isOfficial);
    @AfterMapping
    default void replaceGalleryImageWithTargetGallery(@MappingTarget Gallery gallery){
        gallery.getGalleryImgs().forEach(gI->gI.setGallery(gallery));
    }


}
