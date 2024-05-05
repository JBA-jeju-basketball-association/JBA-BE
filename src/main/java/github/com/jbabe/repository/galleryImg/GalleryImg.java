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
    @ManyToOne(fetch = FetchType.LAZY)
    private Gallery gallery;

    @Column(name = "img_url", nullable = false)
    private String fileUrl;
    @Column(name = "file_name", nullable = false)
    private String fileName;
}
