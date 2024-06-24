package github.com.jbabe.repository.gallery;

import github.com.jbabe.repository.galleryImg.GalleryImg;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.web.dto.gallery.GalleryDetailsDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "gallery")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class Gallery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gallery_id")
    private Integer galleryId;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
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

    @OneToMany(mappedBy = "gallery", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<GalleryImg> galleryImgs;

    public void notifyAndEditSubjectLineContent(GalleryDetailsDto requestModify, Boolean isOfficial, List<GalleryImg> newImgs){
        this.name = requestModify.getTitle();
        if(isOfficial!=null) this.isOfficial = isOfficial;
        this.galleryImgs.addAll(newImgs);
        this.updateAt = LocalDateTime.now();
    }

    @Getter
    public enum GalleryStatus {
        NORMAL, HIDE, DELETE
    }

    public void setUserEmail(String email){
        this.user = new User();
        this.user.setEmail(email);
    }


}
