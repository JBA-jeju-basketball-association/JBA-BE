package github.com.jbabe.repository.competitionImg;

import github.com.jbabe.repository.competition.Competition;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "competition_img")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "competitionImgId")
public class CompetitionImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_img_id")
    private String competitionImgId;

    @JoinColumn(name = "competition_id", nullable = false)
    @ManyToOne
    private Competition competition;

    @Column(name = "img_url", nullable = false)
    private String imgUrl;

    @Column(name = "file_name", nullable = false)
    private String fileName;
}
