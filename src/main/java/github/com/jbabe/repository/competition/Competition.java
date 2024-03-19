package github.com.jbabe.repository.competition;

import github.com.jbabe.repository.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "competition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_id")
    private Integer competitionId;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;

    @Column(name = "competition_name", nullable = false, unique = true)
    private String competitionName;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate; // 대회 시작일

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate; // 대회 종료일

    @Column(name = "related_url")
    private String relatedUrl; // 대회 관련 URL

    @Column(name = "content")
    private String content;

    @Column(name = "competition_status", nullable = false)
    private ScheduleStatus competitionStatus;

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
