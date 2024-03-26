package github.com.jbabe.repository.competitionAttachedFile;

import github.com.jbabe.repository.competition.Competition;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "competition_attached_file")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompetitionAttachedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_attached_file_id")
    private Integer competitionAttachedFileId;

    @JoinColumn(name = "competition_id", nullable = false)
    @ManyToOne
    private Competition competition;

    @Column(name = "file_path", nullable = false)
    private String filePath;
}
