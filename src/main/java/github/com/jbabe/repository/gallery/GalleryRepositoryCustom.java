package github.com.jbabe.repository.gallery;

import github.com.jbabe.web.dto.SearchCriteriaEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GalleryRepositoryCustom {

    Page<Gallery> getGalleryList(Pageable pageable, boolean official);
    Page<Gallery> getGalleryManageList(Pageable pageable, Boolean official, String keyword, SearchCriteriaEnum searchCriteria);
    Page<Gallery> searchGalleryList(boolean official, String keyword, Pageable pageable);




}
