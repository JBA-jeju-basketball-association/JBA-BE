package github.com.jbabe.repository.competition;

import github.com.jbabe.web.dto.competition.CompetitionListResponse;
import github.com.jbabe.web.dto.competition.GetCompetitionAdminListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface CompetitionJpa extends JpaRepository<Competition, Integer>, CompetitionJpaCustom {

    boolean existsByCompetitionName(String competitionName);

    @Query(
            "SELECT new github.com.jbabe.web.dto.competition.CompetitionListResponse(c.competitionId, c.competitionName, " +
                    "CASE WHEN size(c.divisions) = 1 THEN d.divisionName" +
                    "     WHEN size(c.divisions) >= 2 THEN '혼합' END ," +
                    "c.startDate, c.endDate) " +
                    "FROM Competition c " +
                    "LEFT JOIN c.divisions d " +
                    "WHERE " +
                    "c.competitionStatus = :competitionStatus AND " +
                    "CASE " +
                    "           WHEN :status = 'ALL' THEN (c.startDate BETWEEN :startDateFilter AND :endDateFilter) " +
                    "           WHEN :status = 'EXPECTED' THEN (c.startDate > CURRENT_DATE AND (c.startDate BETWEEN :startDateFilter AND :endDateFilter)) " +
                    "           WHEN :status = 'PROCEEDING' THEN (c.startDate < CURRENT_DATE AND c.endDate > CURRENT_DATE AND (c.startDate BETWEEN :startDateFilter AND :endDateFilter)) " +
                    "           WHEN :status = 'COMPLETE' THEN (c.endDate < CURRENT_DATE AND (c.startDate BETWEEN :startDateFilter AND :endDateFilter)) END " +
                    "GROUP BY c.competitionId " +
                    "ORDER BY c.startDate DESC, c.endDate DESC, c.competitionId DESC "
    )
    Page<CompetitionListResponse> findAllCompetitionWithYearPagination(@Param("status") String status, @Param("startDateFilter") Date startDateFilter, @Param("endDateFilter") Date endDateFilter, @Param("competitionStatus") Competition.CompetitionStatus competitionStatus, @Param("pageable") Pageable pageable);

    @Query(
            "SELECT new github.com.jbabe.web.dto.competition.CompetitionListResponse(c.competitionId, c.competitionName, " +
                    "CASE WHEN size(c.divisions) = 1 THEN d.divisionName" +
                    "     WHEN size(c.divisions) >= 2 THEN '혼합' END ," +
                    "c.startDate, c.endDate) " +
                    "FROM Competition c " +
                    "LEFT JOIN c.divisions d " +
                    "WHERE " +
                    "c.competitionStatus = :competitionStatus AND " +
                    "CASE " +
                    "           WHEN :status = 'ALL' THEN TRUE " +
                    "           WHEN :status = 'EXPECTED' THEN (c.startDate > CURRENT_DATE ) " +
                    "           WHEN :status = 'PROCEEDING' THEN (c.startDate <= CURRENT_DATE AND c.endDate >= CURRENT_DATE ) " +
                    "           WHEN :status = 'COMPLETE' THEN (c.endDate < CURRENT_DATE ) " +
                    "END " +
                    "GROUP BY c.competitionId " +
                    "ORDER BY c.startDate DESC, c.endDate DESC, c.competitionId DESC "
    )
    Page<CompetitionListResponse> findAllCompetitionPagination(@Param("status") String status, @Param("competitionStatus") Competition.CompetitionStatus competitionStatus, @Param("pageable") Pageable pageable);

    Page<Competition> findAllByCompetitionStatus(Competition.CompetitionStatus competitionStatus, Pageable pageable);

    List<Competition> findAllByCompetitionStatus(Competition.CompetitionStatus competitionStatus);

    @Query("SELECT new github.com.jbabe.web.dto.competition.GetCompetitionAdminListResponse( " +
            "c.competitionId, u.email, " +
            "CASE " +
            "WHEN CURRENT_DATE < c.startDate THEN '예정' " +
            "WHEN CURRENT_DATE BETWEEN c.startDate AND c.endDate THEN '진행중' " +
            "WHEN CURRENT_DATE > c.endDate THEN '종료' " +
            "END, " +
            "c.phase, " +
            "c.competitionName, c.startDate, c.endDate, c.content, c.relatedUrl, c.competitionStatus, c.createAt, c.updateAt, c.deleteAt) " +
            "FROM Competition c " +
            "LEFT JOIN c.user u " +
            "LEFT JOIN c.divisions d " +
            "WHERE (:searchKey IS NULL OR " +
            "(:searchType = 'title' AND (c.competitionName LIKE %:searchKey%))  OR " +
            "(:searchType = 'email' AND (u.email LIKE %:searchKey%)) OR " +
            "(:searchType = 'id' AND (c.competitionId = :numberSearchKey))) " +
            "AND (:filterStartDate IS NULL OR :filterEndDate IS NULL OR (c.createAt BETWEEN :filterStartDate AND :filterEndDate)) " +
            "AND (:division = '전체' OR d.divisionName = :division) " +
            "AND (:situation = '전체' OR " +
            "(:situation = '예정' AND CURRENT_DATE < c.startDate) OR " +
            "(:situation = '진행중' AND CURRENT_DATE BETWEEN c.startDate AND c.endDate) OR " +
            "(:situation = '종료' AND CURRENT_DATE > c.endDate)) " +
            "GROUP BY c.competitionId " +
            "ORDER BY c.createAt DESC")
    Page<GetCompetitionAdminListResponse> competitionAdminList(String searchType, String searchKey, Integer numberSearchKey, LocalDateTime filterStartDate, LocalDateTime filterEndDate, String division, String situation, Pageable pageable);
}
