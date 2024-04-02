package github.com.jbabe.repository.competitionRecordFile;

import github.com.jbabe.repository.competitionRecord.CompetitionRecord;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "competition_record_file")
public class CompetitionRecordFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_record_file_id")
    private Integer competitionRecordFileId;

    @ManyToOne
    @JoinColumn(name = "competition_record_id", nullable = false)
    private CompetitionRecord competitionRecord;

    @Column(name = "file_path", nullable = false)
    private Integer filePath;
    @Column(name = "file_name", nullable = false)
    private String fileName;
}
