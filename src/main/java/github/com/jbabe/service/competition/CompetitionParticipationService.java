package github.com.jbabe.service.competition;

import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.repository.competitionuser.ParticipationCompetition;
import github.com.jbabe.repository.competitionuser.ParticipationCompetitionFile;
import github.com.jbabe.repository.competitionuser.ParticipationCompetitionRepository;
import github.com.jbabe.repository.competitionuser.ParticipationFileRepository;
import github.com.jbabe.repository.division.Division;
import github.com.jbabe.repository.division.DivisionJpa;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.mapper.CompetitionMapper;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.competition.participate.ModifyParticipateRequest;
import github.com.jbabe.web.dto.competition.participate.ParticipateDetail;
import github.com.jbabe.web.dto.competition.participate.ParticipateRequest;
import github.com.jbabe.web.dto.competition.participate.SimplyParticipateResponse;
import github.com.jbabe.web.dto.infinitescrolling.InfiniteScrollingCollection;
import github.com.jbabe.web.dto.infinitescrolling.criteria.SearchCriteria;
import github.com.jbabe.web.dto.infinitescrolling.criteria.SearchRequest;
import github.com.jbabe.web.dto.participation.ParticipationResponse;
import github.com.jbabe.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetitionParticipationService {
    private final ParticipationCompetitionRepository participationCompetitionRepository;
    private final ParticipationFileRepository participationFileRepository;
    private final DivisionJpa divisionJpa;


    private <T> Competition getCompetitionEntryDate(T requestId) {
        if (requestId instanceof Long) {
            return divisionJpa.getCompetitionEntryDate((Long) requestId)
                    .orElseThrow(() -> new NotFoundException("신청 기록의 대회를 찾을 수 없습니다.", requestId));
        } else if (requestId instanceof Integer) {
            return divisionJpa.getCompetitionEntryDate((Integer) requestId)
                    .orElseThrow(() -> new NotFoundException("종별을 찾을 수 없습니다.", requestId));
        } else {
            throw new IllegalArgumentException("지원하지 않는 ID 타입: " + requestId.getClass().getSimpleName());
        }
    }

    public <T> void checkTheApplicationPeriod(T requestId) {
        Competition competitionEntryDate = getCompetitionEntryDate(requestId);
        LocalDate now = LocalDate.now();
        LocalDate startDate = competitionEntryDate.getParticipationStartDate();
        LocalDate endDate = competitionEntryDate.getParticipationEndDate();

        Map<String, LocalDate> response = new HashMap<>();
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        if(startDate==null||endDate==null) throw new BadRequestException("참가 신청 기간이 미정인 상태 입니다.", response);

        if(now.isBefore(startDate) || now.isAfter(endDate)) throw new BadRequestException("참가 신청 기간이 아닙니다.", response);

    }

    @Transactional
    public long applicationForParticipationInCompetition(Integer divisionId, ParticipateRequest participateRequest, CustomUserDetails customUserDetails) {

        Division division = (Division) createDivisionOrUserById(divisionId.longValue());
        User user = (User) createDivisionOrUserById(customUserDetails.getUserId());

        ParticipationCompetition entity = CompetitionMapper.INSTANCE
                    .participateRequestToParticipationCompetition(participateRequest, division, user);
        try {
            return participationCompetitionRepository.save(entity).getParticipationCompetitionId();
        }catch (DataIntegrityViolationException e) {

            throw new NotFoundException("divisionId가 잘못되었습니다.",  divisionId);
        }
    }

    private <T> Object createDivisionOrUserById(T divisionIdOrUserId) {
        if(divisionIdOrUserId instanceof Long)
            return new Division(((Long) divisionIdOrUserId).intValue());
        else if (divisionIdOrUserId instanceof Integer)
            return new User((Integer) divisionIdOrUserId);
        else {
            throw new BadRequestException("잘못된 요청", divisionIdOrUserId);
        }
    }

    public ParticipateDetail getMyParticipateById(Long participationCompetitionId) {

        List<ParticipationCompetition> entity = participationCompetitionRepository
                .findParticipationCompetitionsByUserIdOrPId(participationCompetitionId, null);
        if(entity.isEmpty())
            throw new NotFoundException("참가신청번호가 잘못되었습니다.", participationCompetitionId);

        return CompetitionMapper.INSTANCE.participationCompetitionToParticipateDetail(entity.get(0));
    }

    public InfiniteScrollingCollection<SimplyParticipateResponse, SearchCriteria> getMyParticipate(CustomUserDetails customUserDetails, SearchRequest searchRequest) {
        if(searchRequest.getIdCursor()!=null) searchRequest.makeCursorHolder();
        List<ParticipationCompetition> entity = participationCompetitionRepository
                .findParticipationCompetitionsByUserIdOrPId(customUserDetails.getUserId(), searchRequest);

        List<SimplyParticipateResponse> response = CompetitionMapper.INSTANCE.participationCompetitionsToParticipateResponse(entity);
        if(!response.isEmpty()) validParticipateList(response.get(0), searchRequest);

        return InfiniteScrollingCollection.of(response, searchRequest.getSize(), searchRequest.getSearchCriteria());
    }
    private void validParticipateList(SimplyParticipateResponse firstResponse, SearchRequest request){
        if (request.getIdCursor()==null) return;
        if(!firstResponse.valueValidity(request))
            throw new BadRequestException("커서의 값, 커서 아이디가 이 전 응답과 일치하지 않습니다.", request);
    }

    @Transactional
    public void deleteParticipate(Long participationCompetitionId, CustomUserDetails customUserDetails) {
        verifyRequestChangePermissions(participationCompetitionId, customUserDetails);
        participationCompetitionRepository.deleteByIdCustom(participationCompetitionId);
    }
    @Transactional
    public void updateParticipate(Long participationCompetitionId, CustomUserDetails customUserDetails, ModifyParticipateRequest participateRequest) {
        verifyRequestChangePermissions(participationCompetitionId, customUserDetails);
        participationCompetitionRepository.updateParticipate(participationCompetitionId, participateRequest);
        List<String> deleteUrls = updateParticipateFilesAndGetDeleteUrls(participationCompetitionId, participateRequest.getFiles());
    }

    private List<String> updateParticipateFilesAndGetDeleteUrls(Long participationCompetitionId, List<FileDto> requestFiles){
        List<String> oldFileUrls = participationFileRepository.findUrlsByParticipationIdCustom(participationCompetitionId);
        System.out.println(requestFiles);
        List<String> newFileUrls = requestFiles.stream().map(file-> file.getFileUrl()).toList();

        List<String> deleteUrls = oldFileUrls.stream().filter(url -> !newFileUrls.contains(url)).toList();

        participationFileRepository.deleteByUrlListCustom(deleteUrls);

        requestFiles.stream()
                .filter(file-> !oldFileUrls.contains(file.getFileUrl()))
                .forEach(file -> makeParticipationFileEntity(file, participationCompetitionId));
        return deleteUrls;
    }
    private void makeParticipationFileEntity(FileDto newFile, Long participationCompetitionId) {
        ParticipationCompetitionFile entity = CompetitionMapper.INSTANCE.fileDtoToParticipationCompetitionFile(newFile);
        entity.setParticipationCompetition(new ParticipationCompetition(participationCompetitionId));
        participationFileRepository.save(entity);
    }

    private void verifyRequestChangePermissions(Long participationCompetitionId, CustomUserDetails customUserDetails) {

    }

    public List<ParticipationResponse> getParticipateListByCompetitionId(Integer competitionId) {
        List<ParticipationResponse> results = participationCompetitionRepository.findParticipationListByCompetitionId(competitionId);
        return  groupFilesByParticipationId(results);
    }

    private List<ParticipationResponse> groupFilesByParticipationId(List<ParticipationResponse> results){

        for(ParticipationResponse result:results){// 리스트 분해
            List<ParticipationResponse.ParticipationDto> participationList = result.getParticipationList();//for 문 안 현재 대회의 리스트
            Map<Long, Integer> mainIdsAndIndices  = new HashMap<>();//메인이될 아이디와 그 객체의 인덱스번호

            for(int i=0; i<participationList.size(); i++){//현재 대회의 요청 리스트를 돔 i는 index 번호
                ParticipationResponse.ParticipationDto participation = participationList.get(i);
                Long participationId = participation.getParticipationId();//for 문 안 현재 요청의 아이디

                if (participation.getFile().getFilePath()==null) {// 파일주소가 null 이라면 파일이 없는 요청이므로 다음 요청으로 넘어감
                    participationList.get(i).setFiles(null);
                    continue;// 파일이 없으므로 중복값도 없으므로 그냥 넘어감
                } else if (mainIdsAndIndices.containsKey(participationId)) {// 이미 중복된 값이 있다면
                    Integer mainIndex = mainIdsAndIndices.get(participationId);//메인이될 객체의 index 넘버
                    participationList.get(mainIndex).getFiles().add(participation.getFile());//메인객체에 파일을 추가
                    participationList.remove(i);//현재 객체 제거
                    i--;//한개가 제거되었으므로 인덱스 넘버 보정
                    continue;
                }
                //파일은 있는데 첫번째로 나온 요청 id 라면
                participation.getFiles().add(participationList.get(i).getFile());//new Array List 필요없음 transform 할때 생성되었음
                mainIdsAndIndices.put(participationId, i);//추후 반복문에서 확인을위해 메인의 id와 index 번호 저장

            }

        }
        return results;
    }

    private List<ParticipationResponse> groupFilesByParticipationIdTest(List<ParticipationResponse> result) {
        for(ParticipationResponse response: result){
            Map<Long, List<ParticipationResponse.ParticipationDto>> grouped = response.getParticipationList().stream()
                    .collect(Collectors.groupingBy(ParticipationResponse.ParticipationDto::getParticipationId));

            grouped.values().stream()
                    .map(dtos->{
                        ParticipationResponse.ParticipationDto main = dtos.get(0);
                        main.setFiles(
                                dtos.stream()
                                        .map(ParticipationResponse.ParticipationDto::getFile)
                                        .toList()
                        );
                        return grouped;
                    });

        }
        return result;

    }
}
