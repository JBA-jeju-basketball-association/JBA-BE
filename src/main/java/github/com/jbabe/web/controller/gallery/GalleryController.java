package github.com.jbabe.web.controller.gallery;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/post")
@RequiredArgsConstructor
public class GalleryController {
    private final GalleryService galleryService;


}
