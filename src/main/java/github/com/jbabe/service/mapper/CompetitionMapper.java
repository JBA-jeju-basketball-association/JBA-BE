package github.com.jbabe.service.mapper;

import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.repository.competitionuser.ParticipationCompetition;
import github.com.jbabe.repository.competitionuser.ParticipationCompetitionFile;
import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.web.dto.competition.participate.ParticipateRequest;
import github.com.jbabe.web.dto.competition.participate.ParticipateResponse;
import github.com.jbabe.web.dto.storage.FileDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CompetitionMapper {
    CompetitionMapper INSTANCE = Mappers.getMapper(CompetitionMapper.class);

    @Mapping(target = "division", source = "division")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "phoneNum", source = "request.phoneNum")
    @Mapping(target = "email", source = "request.email")
    @Mapping(target = "participationCompetitionFiles", source = "request.files")
    ParticipationCompetition participateRequestToParticipationCompetition(ParticipateRequest request, Division division, User user);

    List<ParticipationCompetitionFile> fileDtoListToParticipationCompetitionFileList(List<FileDto> fileDtoList);
    @Mapping(target = "filePath", source = "fileUrl")
    @Mapping(target = "fileName", source = "fileName")
    ParticipationCompetitionFile fileDtoToParticipationCompetitionFile(FileDto file);

    @AfterMapping
    default void insertFilesIntoParticipationCompetition(@MappingTarget ParticipationCompetition participationCompetition) {
        List<ParticipationCompetitionFile> participationCompetitionFiles = participationCompetition.getParticipationCompetitionFiles();
        if (participationCompetitionFiles != null) {
            participationCompetitionFiles.forEach(participationCompetitionFile -> participationCompetitionFile.setParticipationCompetition(participationCompetition));
        }
    }

    @Mapping(target = "competitionName", source = "division.competition.competitionName")
    @Mapping(target = "divisionName", source = "division.divisionName")
    @Mapping(target = "applicantName", source = "name")
    @Mapping(target = "applicantPhoneNum", source = "phoneNum")
    @Mapping(target = "applicantEmail", source = "email")
    @Mapping(target = "applicantDate", source = "createdAt")
    @Mapping(target = "competitionStartDate", source = "division.competition.startDate")
    @Mapping(target = "competitionEndDate", source = "division.competition.endDate")
    @Mapping(target = "participationStartDate", source = "division.competition.participationStartDate")
    @Mapping(target = "participationEndDate", source = "division.competition.participationEndDate")
    @Mapping(target = "fileDtos", source = "participationCompetitionFiles")
    ParticipateResponse participationCompetitionToParticipateResponse(ParticipationCompetition participationCompetition);

    @Mapping(target = "fileUrl", source = "filePath")
    @Mapping(target = "fileName", source = "fileName")
    FileDto participationCompetitionFileToFileDto(ParticipationCompetitionFile participationCompetitionFile);

    List<ParticipateResponse> participationCompetitionsToParticipateResponse(List<ParticipationCompetition> entity);

}
