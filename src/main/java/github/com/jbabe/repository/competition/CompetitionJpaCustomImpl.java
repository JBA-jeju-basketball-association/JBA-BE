package github.com.jbabe.repository.competition;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import github.com.jbabe.repository.competitionAttachedFile.QCompetitionAttachedFile;
import github.com.jbabe.repository.division.QDivision;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.web.dto.competition.CompetitionAdminListRequest;
import github.com.jbabe.web.dto.competition.CompetitionDetailAttachedFile;
import github.com.jbabe.web.dto.competition.GetCompetitionAdminListResponse;
import github.com.jbabe.web.dto.competition.tempDto.ListAndTotalElements;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
public class CompetitionJpaCustomImpl implements CompetitionJpaCustom {
    private final QCompetition qCompetition = QCompetition.competition;
    private final JPAQueryFactory queryFactory;

    private void addSearchType (CompetitionAdminListRequest request , BooleanBuilder builder){
        switch (request.getSearchType()) {
            case TITLE -> {
                if (request.getStringKey() != null)
                        builder.and(qCompetition.competitionName.contains(request.getStringKey()));
            }

            case EMAIL -> builder.and(qCompetition.user.email.eq(request.getStringKey()));
            case ID -> builder.and(qCompetition.competitionId.eq(request.getIntegerKey()));
        }

    }

    private BooleanBuilder getSearchCondition(CompetitionAdminListRequest request) {
        BooleanBuilder builder = new BooleanBuilder();
        addSearchType(request, builder);
        addSearchTimeCondition(request, builder);
        if ( !request.getDivision().equals("전체") ) {
            builder.and(qCompetition.divisions.any().divisionName.eq(request.getDivision()));
        }

        return builder;
    }

    private void addSearchTimeCondition(CompetitionAdminListRequest request, BooleanBuilder builder) {
        if (request.getFilterStartDate() != null) {
            builder.and(qCompetition.endDate.goe(request.getFilterStartDate()));
        }
        if (request.getFilterEndDate() != null) {
            builder.and(qCompetition.startDate.loe(request.getFilterEndDate()));
        }
        if (request.getSituation() != CompetitionAdminListRequest.Situation.ALL) {
            switch (request.getSituation()) {
                case PROGRESS -> builder.and(qCompetition.startDate.loe(LocalDate.now())
                        .and(qCompetition.endDate.goe(LocalDate.now())));
                case END -> builder.and(qCompetition.endDate.loe(LocalDate.now()));
                case READY -> builder.and(qCompetition.startDate.goe(LocalDate.now()));
            }
        }

    }

    private StringExpression getCompetitionStatus() {
        return new CaseBuilder()
                .when(qCompetition.startDate.after(LocalDate.now()))
                .then("예정")
                .when(qCompetition.startDate.loe(LocalDate.now())
                        .and(qCompetition.endDate.goe(LocalDate.now())))
                .then("진행중")
                .otherwise("종료");
    }

    private JPAQuery<Tuple> createCompetitionListSubQuery(BooleanBuilder expression, StringExpression statusExpression){
        return queryFactory.select(
                        qCompetition.competitionId,
                        qCompetition.user.email,
                        qCompetition.phase,
                        qCompetition.competitionName,
                        qCompetition.startDate,
                        qCompetition.endDate,
                        qCompetition.content,
                        qCompetition.relatedUrl,
                        qCompetition.competitionStatus,
                        qCompetition.createAt,
                        qCompetition.updateAt,
                        qCompetition.deleteAt,
                        QDivision.division.divisionName,
                        QCompetitionAttachedFile.competitionAttachedFile,
                        statusExpression)
                .from(qCompetition)
                .leftJoin(qCompetition.divisions, QDivision.division)
                .leftJoin(qCompetition.competitionAttachedFiles, QCompetitionAttachedFile.competitionAttachedFile)
                .where(expression)
                .orderBy(qCompetition.createAt.desc());
    }
    private List<GetCompetitionAdminListResponse> createCompetitionListFinalQuery(JPAQuery<Tuple> subQuery){
        return subQuery.transform(GroupBy.groupBy(qCompetition.competitionId)
                .list(
                        Projections.fields(GetCompetitionAdminListResponse.class,
                                qCompetition.competitionId,
                                qCompetition.user.email.as("userEmail"),
                                qCompetition.phase,
                                qCompetition.competitionName,
                                qCompetition.startDate,
                                qCompetition.endDate,
                                qCompetition.content,
                                qCompetition.relatedUrl.as("link"),
                                qCompetition.competitionStatus.as("status"),
                                qCompetition.createAt,
                                qCompetition.updateAt,
                                qCompetition.deleteAt,
                                GroupBy.set(QDivision.division.divisionName).as("divisions"),
                                GroupBy.set(Projections.fields(CompetitionDetailAttachedFile.class,
                                        QCompetitionAttachedFile.competitionAttachedFile.competitionAttachedFileId,
                                        QCompetitionAttachedFile.competitionAttachedFile.filePath,
                                        QCompetitionAttachedFile.competitionAttachedFile.fileName).skipNulls()).as("files")
                        )));
    }

    private JPAQuery<GetCompetitionAdminListResponse> competitionListCreateDefaultQuery (){
       return queryFactory
               .select(Projections.fields(GetCompetitionAdminListResponse.class,
                       qCompetition.competitionId,
                       qCompetition.user.email.as("userEmail"),
                       qCompetition.phase,
                       qCompetition.competitionName,
                       qCompetition.startDate,
                       qCompetition.endDate,
                       qCompetition.content,
                       qCompetition.relatedUrl.as("link"),
                       qCompetition.competitionStatus.as("status"),
                       qCompetition.createAt,
                       qCompetition.updateAt,
                       qCompetition.deleteAt
               ))
               .from(qCompetition);

    }
    private List<Competition> tuplesToCompetitionsAndSetEmail(List<Tuple> results){
        return results.stream().map(tuple -> {
            Competition c = tuple.get(qCompetition);
            if (c!=null) c.setUser(new User(tuple.get(qCompetition.user.email)));
            return c;
        }).toList();
    }
    @Override
    public Page<GetCompetitionAdminListResponse> findAListOfCompetitionsForAdmin(CompetitionAdminListRequest request) {

        BooleanBuilder expression = getSearchCondition(request);

        JPAQuery<GetCompetitionAdminListResponse> query = competitionListCreateDefaultQuery();

        List<GetCompetitionAdminListResponse> results =  query.where(expression)
                .orderBy(qCompetition.createAt.desc())
                .offset((long) request.getPage() * request.getSize())
                .limit(request.getSize())
                .fetch();

        long total = getTotalElementByCompetitionList(expression);

        return new PageImpl<>(results, PageRequest.of(request.getPage(), request.getSize()), total);
    }
    private long getTotalElementByCompetitionList(BooleanBuilder expression){
        return Optional.ofNullable(queryFactory.select(qCompetition.competitionId.count())
                .from(qCompetition)
                .where(expression)
                .fetchOne()).orElse(0L);
    }


    @Override
    public Map<Integer, List<CompetitionDetailAttachedFile>> getCompetitionFiles(List<Integer> competitionIds) {
        return queryFactory.select(QCompetitionAttachedFile.competitionAttachedFile.competition.competitionId,
                        QCompetitionAttachedFile.competitionAttachedFile.fileName,
                        QCompetitionAttachedFile.competitionAttachedFile.filePath,
                        QCompetitionAttachedFile.competitionAttachedFile.competitionAttachedFileId)
                .from(QCompetitionAttachedFile.competitionAttachedFile)
                .where(QCompetitionAttachedFile.competitionAttachedFile.competition.competitionId.in(competitionIds))
                .fetch()
                .stream().collect(groupingBy(
                        tuple -> Objects.requireNonNull(tuple.get(QCompetitionAttachedFile.competitionAttachedFile.competition.competitionId)),
                        Collectors.mapping(tuple -> new CompetitionDetailAttachedFile(tuple.get(QCompetitionAttachedFile.competitionAttachedFile.competitionAttachedFileId),
                                tuple.get(QCompetitionAttachedFile.competitionAttachedFile.filePath),
                                tuple.get(QCompetitionAttachedFile.competitionAttachedFile.fileName)), Collectors.toList())
                ));
    }
    @Override
    public Map<Integer, List<String>> getDivisionNames(List<Integer> competitionIds) {
        return queryFactory.select(QDivision.division.divisionName, QDivision.division.competition.competitionId)
                .from(QDivision.division)
                .where(QDivision.division.competition.competitionId.in(competitionIds))
                .fetch()
                .stream().collect(groupingBy(
                        tuple -> Objects.requireNonNull(tuple.get(QDivision.division.competition.competitionId)),
                        Collectors.mapping(tuple -> tuple.get(QDivision.division.divisionName), Collectors.toList())
                ));
    }

    @Override
    public List<GetCompetitionAdminListResponse> test() {


        return queryFactory.select(qCompetition.competitionId,
                qCompetition.competitionName,
                qCompetition.divisions,
                qCompetition.competitionAttachedFiles)
                .from(qCompetition)
                .leftJoin(qCompetition.divisions, QDivision.division)
                .leftJoin(qCompetition.competitionAttachedFiles, QCompetitionAttachedFile.competitionAttachedFile)
                .transform(GroupBy.groupBy(qCompetition.competitionId)
                        .list(
                                Projections.fields(GetCompetitionAdminListResponse.class,
                                        qCompetition.competitionId,
                                        qCompetition.competitionName,
                                        GroupBy.set(QDivision.division.divisionName).as("divisions"),
                                        GroupBy.set(Projections.fields(CompetitionDetailAttachedFile.class,
                                                QCompetitionAttachedFile.competitionAttachedFile.competitionAttachedFileId,
                                                QCompetitionAttachedFile.competitionAttachedFile.filePath,
                                                QCompetitionAttachedFile.competitionAttachedFile.fileName).skipNulls()).as("files")

                                )));
    }
}
