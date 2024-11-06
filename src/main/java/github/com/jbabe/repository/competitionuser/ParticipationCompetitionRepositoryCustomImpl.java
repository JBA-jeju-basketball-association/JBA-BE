package github.com.jbabe.repository.competitionuser;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import github.com.jbabe.repository.competition.QCompetition;
import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.division.QDivision;
import github.com.jbabe.repository.user.QUser;
import github.com.jbabe.repository.user.User;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@RequiredArgsConstructor
public class ParticipationCompetitionRepositoryCustomImpl implements ParticipationCompetitionRepositoryCustom {
    private final QParticipationCompetition QPARTICIPATIONCOMPETITION = QParticipationCompetition.participationCompetition;

    private final JPAQueryFactory queryFactory;
    @Override
    public List<ParticipationCompetition> findParticipationCompetitionsByUserId(Integer userId, boolean isAll) {
        QCompetition competition = QCompetition.competition;


        return queryFactory.selectFrom(QPARTICIPATIONCOMPETITION)
                .join(QPARTICIPATIONCOMPETITION.division, QDivision.division).fetchJoin()
                .join(QDivision.division.competition, competition).fetchJoin()
                .leftJoin(QPARTICIPATIONCOMPETITION.participationCompetitionFiles, QParticipationCompetitionFile.participationCompetitionFile).fetchJoin()
                .where(mySearchConditions(userId, isAll, competition))
                .fetch();

    }
    private BooleanExpression mySearchConditions(Integer userId, boolean isAll, QCompetition competition) {
        BooleanExpression expression = QPARTICIPATIONCOMPETITION.user.userId.eq(userId);
        if(!isAll){
            LocalDate now = LocalDate.now();
            BooleanExpression dateCondition = competition.participationStartDate.before(now)
                    .and(competition.participationEndDate.after(now));
            expression = expression.and(dateCondition);
        }
        return expression;
    }
}
