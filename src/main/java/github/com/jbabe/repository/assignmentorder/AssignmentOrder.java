package github.com.jbabe.repository.assignmentorder;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode
public class AssignmentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_order_id")
    private Long assignmentOrderId;
    @Column(name = "order_number")
    private Long orderNumber;
    @Column(name = "competition_date")
    private LocalDate competitionDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    private String title;
    private String content;
    @Column(name = "is_referee")
    private Boolean isReferee;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
