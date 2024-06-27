package github.com.jbabe.repository.postImg;

import github.com.jbabe.repository.post.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_img")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_img_id")
    private Integer postImgId;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Column(name = "img_url", nullable = false)
    private String imgUrl;
    @Column(name = "file_name", nullable = false)
    private String fileName;
}
