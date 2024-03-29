package github.com.jbabe.repository.post;

import github.com.jbabe.repository.postAttachedFile.PostAttachedFile;
import github.com.jbabe.repository.postImg.PostImg;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Entity
@Table(name = "post")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "postId")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
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

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    @OneToMany(mappedBy = "post")
    private Set<PostAttachedFile> postAttachedFiles;

    @OneToMany(mappedBy = "post")
    private Set<PostImg> postImgs;


    @Getter
    public enum PostStatus{
        NORMAL, HIDE, DELETE
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
}
