package github.com.jbabe.repository.competitionuser;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import github.com.jbabe.repository.competition.QCompetition;
import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.division.QDivision;
import github.com.jbabe.repository.user.QUser;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.web.dto.competition.participate.ModifyParticipateRequest;
import github.com.jbabe.web.dto.competition.participate.ParticipateRequest;
import github.com.jbabe.web.dto.infinitescrolling.criteria.CursorHolder;
import github.com.jbabe.web.dto.infinitescrolling.criteria.SearchRequest;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class ParticipationCompetitionRepositoryCustomImpl implements ParticipationCompetitionRepositoryCustom {
    private final QParticipationCompetition QPARTICIPATIONCOMPETITION = QParticipationCompetition.participationCompetition;

    private final JPAQueryFactory queryFactory;

    @Override
    public <T> List<ParticipationCompetition> findParticipationCompetitionsByUserIdOrPId(T userOrParticipateId, SearchRequest searchRequest) {
        boolean isListRequest = userOrParticipateId instanceof Integer;
        BooleanExpression expression = mySearchConditions(userOrParticipateId);
        if (isListRequest && searchRequest.getIdCursor()!=null)
            expression = expression.and(addConditionForScrolling(searchRequest));

        JPAQuery<ParticipationCompetition> query = queryFactory.selectFrom(QPARTICIPATIONCOMPETITION)
                .join(QPARTICIPATIONCOMPETITION.division, QDivision.division).fetchJoin()
                .join(QDivision.division.competition, QCompetition.competition).fetchJoin();
        if (!isListRequest)
            query.leftJoin(QPARTICIPATIONCOMPETITION.participationCompetitionFiles, QParticipationCompetitionFile.participationCompetitionFile).fetchJoin();
        query.where(expression);
        if (isListRequest)
            query.orderBy(QPARTICIPATIONCOMPETITION.createdAt.desc(), QPARTICIPATIONCOMPETITION.participationCompetitionId.desc());
        return query.fetch();

    }

    private BooleanExpression addConditionForScrolling(SearchRequest request) {
        CursorHolder holder = request.getCursorHolder();
        return QPARTICIPATIONCOMPETITION.createdAt.before(holder.getCreatedAtCursor())
                .or(QPARTICIPATIONCOMPETITION.createdAt.eq(holder.getCreatedAtCursor())
                        .and(QPARTICIPATIONCOMPETITION.participationCompetitionId.loe(holder.getIdCursor())));
    }

    @Override
    public Optional<Integer> findParticipationCompetitionTheUserIdOfById(Long participationCompetitionId) {
       return Optional.ofNullable(queryFactory.select(QPARTICIPATIONCOMPETITION.user.userId)
                .from(QPARTICIPATIONCOMPETITION)
                .where(QPARTICIPATIONCOMPETITION.participationCompetitionId.eq(participationCompetitionId))
                .fetchOne());
//
//        List<Tuple> tuples = queryFactory.select(QPARTICIPATIONCOMPETITION,
//                        QParticipationCompetitionFile.participationCompetitionFile.participationCompetitionFileId,
//                        QPARTICIPATIONCOMPETITION.user.userId)
//                .from(QPARTICIPATIONCOMPETITION)
//                .leftJoin(QPARTICIPATIONCOMPETITION.participationCompetitionFiles, QParticipationCompetitionFile.participationCompetitionFile)
//                .where(QPARTICIPATIONCOMPETITION.participationCompetitionId.eq(participationCompetitionId))
//                .fetch();
//
//        return Optional.ofNullable(tuplesToDeleteParticipateEntity(tuples));
    }

    @Override
    public void deleteByIdCustom(Long participationCompetitionId) {
        queryFactory.delete(QPARTICIPATIONCOMPETITION)
                .where(QPARTICIPATIONCOMPETITION.participationCompetitionId.eq(participationCompetitionId))
                .execute();
    }

    @Override
    public void updateParticipate(Long participationCompetitionId, ModifyParticipateRequest participateRequest) {
        JPAUpdateClause updateClause = queryFactory.update(QPARTICIPATIONCOMPETITION)
                .set(QPARTICIPATIONCOMPETITION.name, participateRequest.getName())
                .set(QPARTICIPATIONCOMPETITION.phoneNum, participateRequest.getPhoneNum())
                .set(QPARTICIPATIONCOMPETITION.email, participateRequest.getEmail())
                .set(QPARTICIPATIONCOMPETITION.updatedAt, LocalDateTime.now());
        if (participateRequest.getDivisionId() != null) {
            updateClause.set(QPARTICIPATIONCOMPETITION.division, new Division(participateRequest.getDivisionId()));
        }

        updateClause.where(QPARTICIPATIONCOMPETITION.participationCompetitionId.eq(participationCompetitionId))
                .execute();
    }

    //    private ParticipationCompetition tupleOneToDeleteParticipateEntity(Tuple tuple){
//        if (tuple != null) {
//            ParticipationCompetition entity = tuple.get(QPARTICIPATIONCOMPETITION);
//            Objects.requireNonNull(entity).setUser(new User(tuple.get(QPARTICIPATIONCOMPETITION.user.userId)));
//            return entity;
//        }
//
//        return null;
//    }
    private ParticipationCompetition tuplesToDeleteParticipateEntity(List<Tuple> tuples) {
        if (tuples != null) {
            ParticipationCompetition entity = tuples.get(0).get(QPARTICIPATIONCOMPETITION);
            Objects.requireNonNull(entity).setUser(new User(tuples.get(0).get(QPARTICIPATIONCOMPETITION.user.userId)));
            entity.setParticipationCompetitionFiles(new ArrayList<>());
            tuples.stream().filter(tuple -> tuple.get(QParticipationCompetitionFile.participationCompetitionFile.participationCompetitionFileId) != null)
                    .forEach(tuple -> entity.getParticipationCompetitionFiles()
                            .add(new ParticipationCompetitionFile(tuple.get(QParticipationCompetitionFile.participationCompetitionFile.participationCompetitionFileId))));
            return entity;
        }

        return null;
    }


    private <T> BooleanExpression mySearchConditions(T id) {
        if(id instanceof Long){
            return QPARTICIPATIONCOMPETITION.participationCompetitionId.eq((Long) id);
        }else if (id instanceof Integer){
            return QPARTICIPATIONCOMPETITION.user.userId.eq((Integer) id);
        }else {
            throw new BadRequestException("잘못된 요청", id);
        }
    }
}
