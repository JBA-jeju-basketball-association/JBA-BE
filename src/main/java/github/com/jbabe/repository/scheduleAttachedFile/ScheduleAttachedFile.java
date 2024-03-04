package github.com.jbabe.repository.scheduleAttachedFile;

import github.com.jbabe.repository.schedule.Schedule;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "schedule_attached_file")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleAttachedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_attached_file_id")
    private Integer scheduleAttachedFileId;

    @JoinColumn(name = "schedule_id", nullable = false)
    @ManyToOne
    private Schedule schedule;

    @Column(name = "file_path", nullable = false)
    private String filePath;
}
