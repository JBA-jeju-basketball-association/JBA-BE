package github.com.jbabe.repository.division;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.repository.competition.QCompetition;
import github.com.jbabe.repository.competitionuser.QParticipationCompetition;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class DivisionJpaCustomImpl implements DivisionJpaCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Integer> getDivisionIdByDivisionName(String divisionName) {
        return Optional.ofNullable(queryFactory.select(QDivision.division.divisionId)
                .from(QDivision.division)
                .where(QDivision.division.divisionName.eq(divisionName))
                .fetchOne());
    }

    @Override
    public Optional<Competition> getCompetitionEntryDate(Integer divisionId) {
            return Optional.ofNullable(queryFactory.select(Projections.fields(Competition.class,
                            QCompetition.competition.participationEndDate,
                            QCompetition.competition.participationStartDate))
                    .from(QDivision.division)
                    .where(QDivision.division.divisionId.eq(divisionId))
                    .join(QDivision.division.competition, QCompetition.competition)
                    .fetchOne());
    }
    @Override
    public Optional<Competition> getCompetitionEntryDate(Long participationId){
        return Optional.ofNullable(queryFactory.select(Projections.fields(Competition.class,
                        QCompetition.competition.participationEndDate,
                        QCompetition.competition.participationStartDate))
                .from(QDivision.division)
                .join(QDivision.division.participationCompetitions, QParticipationCompetition.participationCompetition)
                .join(QDivision.division.competition, QCompetition.competition)
                .where(QParticipationCompetition.participationCompetition.participationCompetitionId.eq(participationId))
                .fetchOne());
    }
}
