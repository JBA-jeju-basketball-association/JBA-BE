package github.com.jbabe.repository.post;

import github.com.jbabe.repository.postAttachedFile.PostAttachedFile;
import github.com.jbabe.repository.postImg.PostImg;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.web.dto.post.PostModifyDto;
import github.com.jbabe.web.dto.storage.FileDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "post")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "postId")
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "content")
    private String content;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @Column(name = "post_status", nullable = false)

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    @Column(name = "is_announcement", nullable = false)
    private Boolean isAnnouncement; // 공지여부 ==> true : 공지, false : 일반

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<PostAttachedFile> postAttachedFiles;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<PostImg> postImgs;

    @Column(name = "foreword")
    private String foreword;

//    public void defaultValue() {
//        this.viewCount = 0;
//        this.createAt = LocalDateTime.now();
//        if (this.isAnnouncement == null) this.isAnnouncement = false;
//        this.postStatus = PostStatus.NORMAL;
//    }

    public void notifyAndEditSubjectLineContent(PostModifyDto postModifyDto, Boolean isOfficial) {
        if(isOfficial!=null) this.isAnnouncement = isOfficial;
        this.name = postModifyDto.getTitle();
        this.content = postModifyDto.getContent();
        this.foreword = postModifyDto.getForeword();
        addFiles(postModifyDto.getRemainingFiles(), postModifyDto.getPostImgs());
        this.updateAt = LocalDateTime.now();

    }

    public void updateIsAnnouncement() {
        this.isAnnouncement = !this.isAnnouncement;
    }


    @Getter
    public enum PostStatus{
        NORMAL("normal"), HIDE("hide"), DELETE("delete");
        private final String path;
        PostStatus(String path){
            this.path=path;
        }
    }
    @Getter
    public enum Category{
        NOTICE("notice"), NEWS("news"), LIBRARY("library");
        private final String path;
        Category(String path){
            this.path=path;
        }
        public static Category pathToEnum(String path){
            for(Category c: EnumSet.allOf(Category.class)) if(c.path.equals(path)) return c;
            throw new BadRequestException("Category Incorrectly Entered", path);
        }
    }

    public void increaseViewCount(){
        this.viewCount++;
    }

    public void addFiles(List<FileDto> files, List<FileDto> imgs){
        if(this.postImgs == null) {
            this.postImgs = new HashSet<>();
        }
        if(this.postAttachedFiles == null) {
            this.postAttachedFiles = new HashSet<>();
        }
        if(files!=null && !files.isEmpty() ){
            for(FileDto f: files) {
                PostAttachedFile postAttachedFile = PostAttachedFile.builder()
                        .post(this)
                        .filePath(f.getFileUrl())
                        .fileName(f.getFileName()).build();
                this.postAttachedFiles.add(postAttachedFile);
            }
        }
        if(imgs!=null && !imgs.isEmpty()) {
            for (FileDto img : imgs) {
                PostImg postImg = PostImg.builder()
                        .post(this)
                        .fileName(img.getFileName())
                        .imgUrl(img.getFileUrl()).build();
                this.postImgs.add(postImg);
            }
        }
    }
}
