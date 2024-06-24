package github.com.jbabe.repository.postAttachedFile;

import github.com.jbabe.repository.post.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_attached_file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostAttachedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_attached_file_id")
    private Integer postAttachedFileId;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne
    private Post post;

    @Column(name = "file_path", nullable = false)
    private String filePath;
    @Column(name = "file_name", nullable = false)
    private String fileName;

    public PostAttachedFile(Integer postId, String filePath, String fileName) {
        this.post = Post.builder().postId(postId).build();
        this.filePath = filePath;
        this.fileName = fileName;
    }
}
