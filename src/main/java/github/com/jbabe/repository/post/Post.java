package github.com.jbabe.repository.post;

import github.com.jbabe.repository.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "content")
    private String content;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @Column(name = "post_status", nullable = false)
    private PostStatus postStatus;

    @Column(name = "is_announcement", nullable = false)
    private Boolean isAnnouncement; // 공지여부 ==> true : 공지, false : 일반

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    @Getter
    public enum PostStatus{
        NORMAL, HIDE, DELETE
    }
}
