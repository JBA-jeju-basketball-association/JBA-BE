package github.com.jbabe.repository.video;

import github.com.jbabe.repository.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "video")
@Getter
@Setter
@EqualsAndHashCode(of = "videoId")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Video {
    @Id @Column(name = "video_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer videoId;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "content")
    private String content;

    @Column(name = "is_official", nullable = false)
    private Boolean isOfficial;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private VideoStatus videoStatus;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    @Getter
    public enum VideoStatus {
        NORMAL, HIDE, DELETE
    }
}


