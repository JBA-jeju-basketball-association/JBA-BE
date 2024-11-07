package github.com.jbabe.repository.competitionuser;

import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.mapper.CompetitionMapper;
import github.com.jbabe.web.dto.competition.participate.ParticipateRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "participation_competition")
@Setter
@Getter
@NoArgsConstructor
public class ParticipationCompetition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_competition_id")
    private Long participationCompetitionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_id")
    private Division division;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    private String name;
    @Column(name = "phone_num")
    private String phoneNum;
    private String email;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "participationCompetition", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipationCompetitionFile> participationCompetitionFiles;

    public void setSomeFiled(ParticipateRequest participateRequest) {
        this.name = participateRequest.getName();
        this.phoneNum = participateRequest.getPhoneNum();
        this.email = participateRequest.getEmail();
        this.participationCompetitionFiles = CompetitionMapper.INSTANCE.fileDtoListToParticipationCompetitionFileList(participateRequest.getFiles());
        this.createdAt = LocalDateTime.now();
    }

    public ParticipationCompetition(Long id){
        this.participationCompetitionId = id;
    }
}
