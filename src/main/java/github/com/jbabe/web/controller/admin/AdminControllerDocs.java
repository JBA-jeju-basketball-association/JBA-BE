package github.com.jbabe.web.controller.admin;

import github.com.jbabe.web.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;

@Tag(name = "AdminUser", description = "관리자 API")
public interface AdminControllerDocs {

    @Operation(summary = "유저 목록 조회", description = "관리할 유저목록 조회 API")
    ResponseDto getUserInfo(@Parameter(description = "페이지 쪽수 (기본값 = 0)") int page,
                            @Parameter(description = "페이지당 보여질 갤러리게시물 갯수 (기본값 = 20)") int size,
                            String keyword,
                            @Parameter(description = "검색 기준 (기본값 = null) 검색어가 있을시 검색 기준은 필수 입니다.", examples = {
                                    @ExampleObject(name = "전체", value = "null", description = "전체 조회"),
                                    @ExampleObject(name = "이메일", value = "email", description = "이메일로 검색"),
                                    @ExampleObject(name = "이름", value = "name", description = "이름으로 검색"),
                                    @ExampleObject(name = "유저 아이디", value = "id", description = "유저 고유번호로 검색"),
                                    @ExampleObject(name = "팀", value = "team", description = " 팀으로 검색")}) String searchCriteriaString,
                            @Parameter(description = "유저 권한 권한별 조회시 사용", examples = {
                                    @ExampleObject(name = "전체", value = "null", description = "전체 조회"),
                                    @ExampleObject(name = "ROLE_USER", value = "user", description = "유저 권한으로 조건 검색"),
                                    @ExampleObject(name = "ROLE_MASTER", value = "master", description = "마스터 권한 조건 검색"),
                                    @ExampleObject(name = "ROLE_ADMIN", value = "admin", description = "운영자 권한 조건 검색"),
                                    @ExampleObject(name = "ROLE_REFEREE_LEADER", value = "referee-leader", description = "레프리 리더 조건 검색"),
                                    @ExampleObject(name = "ROLE_REFEREE", value = "referee", description = "레프리조건검색"),
                                    @ExampleObject(name = "ROLE_TABLE_OFFICIAL_LEADER", value = "table-official-leader", description = "테이블 오피셜 리더 조건검색"),
                                    @ExampleObject(name = "ROLE_TABLE_OFFICIAL", value = "table-official", description = " 테이블 오피셜 조건 검색")}) String permissionsStr,
                            @Parameter(description = "검색 시작 날짜 ex) yyyy-MM-dd<br>" +
                                    "종료날짜 값이 있는데 시작날짜가 null인 경우 2024-01-01 로 지정되고<br> " +
                                    "현재 시간 또는 종료일 보다 늦을 경우 400 에러 발생 합니다.<br>" +
                                    "시작, 종료 둘다 값이 없을경우 전체 기간 조회"
                                    , example = "2022-07-01")
                            LocalDate startDate,
                            @Parameter(description = "검색 마지막 날짜 ex) yyyy-MM-dd<br>" +
                                    "시작날짜 값이 있는데 마지막날짜가 null인 경우 현재 날짜로 지정되고<br>" +
                                    "현재 시간 보다 늦은 날짜로 검색 시도시 400 에러 발생 합니다.", example = "2022-07-31")
                            LocalDate endDate);
}
