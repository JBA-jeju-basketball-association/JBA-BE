package github.com.jbabe.service.competition;

import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.repository.competition.CompetitionJpa;
import github.com.jbabe.repository.division.DivisionEnum;
import github.com.jbabe.repository.division.DivisionEnumJpa;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.web.dto.competition.CompetitionListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionJpa competitionJpa;
    private final DivisionEnumJpa divisionEnumJpa;

    public Page<CompetitionListResponse> getCompetitionList(String status, String year, Pageable pageable) {


        Page<CompetitionListResponse> competitionListResponses;
        if (year == null) {
            competitionListResponses = competitionJpa.findAllCompetitionPagination(status, Competition.CompetitionStatus.NORMAL, pageable);
        }else {
            Date startDateFilter = Competition.getStartTimeThisYear(year);
            Date endDateFilter = Competition.getEndTimeThisYear(year);
            competitionListResponses = competitionJpa.findAllCompetitionWithYearPagination(status, startDateFilter, endDateFilter, Competition.CompetitionStatus.NORMAL, pageable);
        }

//        if (competitionListResponses.getTotalElements() == 0) throw new NotFoundException("totalElement is 0", "");
        return competitionListResponses;
    }




    public List<Integer> getCompetitionYearList() {
        List<Competition> competitions = competitionJpa.findAllByCompetitionStatus(Competition.CompetitionStatus.NORMAL);
        if (competitions.isEmpty()) throw new NotFoundException("대회를 찾을 수 없습니다.", null);
        List<Integer> yearList = new ArrayList<>();
        competitions.forEach((c) -> {
//                    Calendar calendar = Calendar.getInstance();
//            Date startDate = Date.from(c.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant());

//            calendar.setTime(startDate);
//                    int year = calendar.get(Calendar.YEAR);
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTime(c.getStartDate());
                    int year = c.getStartDate().getYear();
                    if (!yearList.contains(year)) yearList.add(year);
                }
        );
        return yearList;
    }


    public List<String> getCompetitionDivisionList() {
        List<DivisionEnum> divisions = divisionEnumJpa.findAll();
        return divisions.stream().map(DivisionEnum::getDivisionName).toList();
    }
}
