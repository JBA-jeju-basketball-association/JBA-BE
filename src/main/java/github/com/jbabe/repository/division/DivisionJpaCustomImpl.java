package github.com.jbabe.repository.division;

import com.querydsl.jpa.impl.JPAQueryFactory;
import github.com.jbabe.repository.competition.Competition;
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
    public Competition getCompetitionEntryDateByDivisionId(Long divisionId) {
        return null;
    }
}
