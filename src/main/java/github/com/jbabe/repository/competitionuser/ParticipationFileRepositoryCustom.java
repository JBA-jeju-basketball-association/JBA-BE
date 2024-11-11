package github.com.jbabe.repository.competitionuser;

import github.com.jbabe.web.dto.storage.FileDto;

import java.util.List;

public interface ParticipationFileRepositoryCustom {
    List<String> findUrlsByParticipationIdCustom(Long participationCompetitionId);

    void deleteByUrlCustom(String url);

    void saveCustom(FileDto file, Long participationCompetitionId);
}
