package github.com.jbabe.repository.schedule;

import github.com.jbabe.repository.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "divition", nullable = false)
    private String division; // 대회 종별

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate; // 대회 시작일

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate; // 대회 종료일

    @Column(name = "place", nullable = false)
    private String place;

    @Column(name = "related_url")
    private String relatedUrl; // 대회 관련 URL

    @Column(name = "content")
    private String content;

    @Column(name = "schedule_status", nullable = false)
    private ScheduleStatus scheduleStatus;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;


    @Getter
    public enum ScheduleStatus {
        NORMAL, HIDE, DELETE
    }
}
