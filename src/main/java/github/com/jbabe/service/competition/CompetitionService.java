package github.com.jbabe.service.competition;

import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.repository.competition.CompetitionJpa;
import github.com.jbabe.repository.competitionAttachedFile.CompetitionAttachedFile;
import github.com.jbabe.repository.competitionAttachedFile.CompetitionAttachedFileJpa;
import github.com.jbabe.repository.competitionImg.CompetitionImg;
import github.com.jbabe.repository.competitionImg.CompetitionImgJpa;
import github.com.jbabe.repository.competitionPlace.CompetitionPlace;
import github.com.jbabe.repository.competitionPlace.CompetitionPlaceJpa;
import github.com.jbabe.repository.competitionRecord.CompetitionRecord;
import github.com.jbabe.repository.competitionRecord.CompetitionRecordJpa;
import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.division.DivisionEnum;
import github.com.jbabe.repository.division.DivisionEnumJpa;
import github.com.jbabe.repository.division.DivisionJpa;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.ConflictException;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.storage.StorageService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.competition.*;
import github.com.jbabe.web.dto.storage.FileDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
                    Calendar calendar = Calendar.getInstance();
            Date startDate = Date.from(c.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant());

            calendar.setTime(startDate);
                    int year = calendar.get(Calendar.YEAR);
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
