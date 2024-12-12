package github.com.jbabe.repository.competition;

import github.com.jbabe.web.dto.competition.CompetitionAdminListRequest;
import github.com.jbabe.web.dto.competition.CompetitionDetailAttachedFile;
import github.com.jbabe.web.dto.competition.GetCompetitionAdminListResponse;
import github.com.jbabe.web.dto.competition.tempDto.ListAndTotalElements;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface CompetitionJpaCustom {
    Page<GetCompetitionAdminListResponse>  findAListOfCompetitionsForAdmin(CompetitionAdminListRequest request);

    Map<Integer, List<CompetitionDetailAttachedFile>> getCompetitionFiles(List<Integer> competitionIds);

    Map<Integer, List<String>> getDivisionNames(List<Integer> competitionIds);

    List<GetCompetitionAdminListResponse> test();

}
