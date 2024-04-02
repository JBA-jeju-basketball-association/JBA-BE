package github.com.jbabe.repository.galleryImg;

import github.com.jbabe.repository.gallery.Gallery;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gallery_img")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gallery_img_id")
    private Integer galleryImgId;

    @JoinColumn(name = "gallery_id", nullable = false)
    @ManyToOne
    private Gallery gallery;

    @Column(name = "img_url", nullable = false)
    private String imgUrl;


}
