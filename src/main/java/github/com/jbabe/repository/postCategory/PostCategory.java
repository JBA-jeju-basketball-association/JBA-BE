package github.com.jbabe.repository.postCategory;

import github.com.jbabe.repository.post.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_category_id")
    private Integer postCategoryId;

    @JoinColumn(name = "post_id", nullable = false)
    @OneToOne
    private Post post;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Getter
    public enum Category{
        ANNOUNCEMENT, COMPETITION_ANNOUNCEMENT, REFERENCE_ROOM, NEWS, REFEREE_ANNOUNCEMENT, TABLE_OFFICIAL_ANNOUNCEMENT
    }
}
