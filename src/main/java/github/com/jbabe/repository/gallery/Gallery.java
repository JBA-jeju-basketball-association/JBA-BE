package github.com.jbabe.repository.gallery;

import github.com.jbabe.repository.galleryImg.GalleryImg;
import github.com.jbabe.repository.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "gallery")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gallery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gallery_id")
    private Integer galleryId;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "is_official", nullable = false)
    private Boolean isOfficial; // false : 갤러리, true : 스탭갤러리

    @Column(name = "gallery_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GalleryStatus galleryStatus;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    @OneToMany(mappedBy = "gallery")
    private List<GalleryImg> galleryImgs;

    @Getter
    public enum GalleryStatus {
        NORMAL, HIDE, DELETE
    }


}
