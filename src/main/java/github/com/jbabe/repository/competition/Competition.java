package github.com.jbabe.repository.competition;

import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private Date startDate; // 대회 시작일

    @Column(name = "end_date", nullable = false)
    private Date endDate; // 대회 종료일

    @Column(name = "related_url")
    private String relatedUrl; // 대회 관련 URL

    @Column(name = "content")
    private String content;

    @Column(name = "phase", nullable = false)
    @Enumerated(EnumType.STRING)
    private Phase phase;

    @Column(name = "competition_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CompetitionStatus competitionStatus;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    @OneToMany(mappedBy = "competition", fetch = FetchType.LAZY)
    private List<Division> divisions;


    @Getter
    public enum CompetitionStatus {
        NORMAL, HIDE, DELETE
    }

    @Getter
    public enum Phase {
        INFO, SCHEDULE, ASSIGN_REGISTRATION, ASSIGN_APPLICATION, ASSIGN_COMPLETE, FINISH
    }

    public static Date getStartTimeThisYear(String year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTime();
    }

    public static Date getEndTimeThisYear(String year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        return calendar.getTime();
    }
}

