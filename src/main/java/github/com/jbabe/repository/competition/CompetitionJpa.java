package github.com.jbabe.repository.competition;

import github.com.jbabe.web.dto.competition.CompetitionListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface CompetitionJpa extends JpaRepository<Competition, Integer> {
    @Query(
            "SELECT new github.com.jbabe.web.dto.competition.CompetitionListResponse(c.competitionId, c.competitionName, " +
                    "CASE WHEN size(c.divisions) = 1 THEN d.divisionName" +
                    "     WHEN size(c.divisions) >= 2 THEN '혼합' END ," +
                    "c.startDate, c.endDate) " +
                    "FROM Competition c " +
                    "LEFT JOIN c.divisions d " +
                    "WHERE CASE " +
                    "           WHEN :status = 'ALL' THEN (c.startDate BETWEEN :startDateFilter AND :endDateFilter) " +
                    "           WHEN :status = 'EXPECTED' THEN (c.startDate > CURRENT_DATE AND (c.startDate BETWEEN :startDateFilter AND :endDateFilter)) " +
                    "           WHEN :status = 'PROCEEDING' THEN (c.startDate < CURRENT_DATE AND c.endDate > CURRENT_DATE AND (c.startDate BETWEEN :startDateFilter AND :endDateFilter)) " +
                    "           WHEN :status = 'COMPLETE' THEN (c.endDate < CURRENT_DATE AND (c.startDate BETWEEN :startDateFilter AND :endDateFilter)) END " +
                    "GROUP BY c.competitionId " +
                    "ORDER BY c.startDate DESC, c.competitionId DESC "
    )
    Page<CompetitionListResponse> findAllCompetitionPagination(String status, Date startDateFilter, Date endDateFilter, Pageable pageable);
}
