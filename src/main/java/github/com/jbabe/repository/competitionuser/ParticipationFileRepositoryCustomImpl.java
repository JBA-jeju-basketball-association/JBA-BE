package github.com.jbabe.repository.competitionuser;

import com.querydsl.jpa.impl.JPAQueryFactory;
import github.com.jbabe.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;

import java.util.List;
@RequiredArgsConstructor
public class ParticipationFileRepositoryCustomImpl implements ParticipationFileRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final QParticipationCompetitionFile FILE = QParticipationCompetitionFile.participationCompetitionFile;
    @Override
    public List<String> findUrlsByParticipationIdCustom(Long participationCompetitionId) {
        return queryFactory.select(FILE.filePath)
                .from(FILE)
                .where(FILE.participationCompetition.participationCompetitionId.eq(participationCompetitionId))
                .fetch();

    }

    @Override
    public void deleteByUrlCustom(String url) {
        queryFactory.delete(FILE)
                .where(FILE.filePath.eq(url))
                .execute();
    }

    @Override
    public void saveCustom(FileDto file, Long participationCompetitionId) {
        queryFactory.insert(FILE)
                .columns(FILE.filePath, FILE.fileName, FILE.participationCompetition)
                .values(file.getFileUrl(), file.getFileName(), new ParticipationCompetition(participationCompetitionId))
                .execute();
    }
}
