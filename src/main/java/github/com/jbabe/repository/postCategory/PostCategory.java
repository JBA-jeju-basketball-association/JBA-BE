package github.com.jbabe.repository.postCategory;

import com.fasterxml.jackson.annotation.JsonCreator;
import github.com.jbabe.repository.post.Post;
import github.com.jbabe.service.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

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
        ASSOCIATION_ANNOUNCEMENT("association-announcement"),
        COMPETITION_ANNOUNCEMENT("competition-announcement"),
        REFERENCE_ROOM("reference-room"),
        NEWS("news"),
        REFEREE_ANNOUNCEMENT("referee-announcement"),
        TABLE_OFFICIAL_ANNOUNCEMENT("table-official-announcement");
        private final String value;
        Category(String value){
            this.value = value;
        }


        public static Category inValue(String category){
            for(Category c : EnumSet.allOf(Category.class)){
                if(c.value.equals(category)) return c;
            }
            throw new BadRequestException("Enum Mismatch Category", category);
        }

    }
}
