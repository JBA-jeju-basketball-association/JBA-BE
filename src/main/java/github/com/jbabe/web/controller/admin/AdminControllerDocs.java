package github.com.jbabe.web.controller.admin;

import github.com.jbabe.web.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @Operation(summary = "유저 권한 수정", description = "유저 권한 수정 API")
    ResponseDto updateUserPermission(@Parameter(description = "유저 고유 번호 userId (필수 값으로 null은 허용 안됨)") int userId,   @Parameter(description = "유저 권한 수정될 유저의 권한 (필수 값으로 null은 허용 안됨)", examples = {
            @ExampleObject(name = "ROLE_USER", value = "user", description = "유저 권한으로 조건 검색"),
            @ExampleObject(name = "ROLE_MASTER", value = "master", description = "마스터 권한 조건 검색"),
            @ExampleObject(name = "ROLE_ADMIN", value = "admin", description = "운영자 권한 조건 검색"),
            @ExampleObject(name = "ROLE_REFEREE_LEADER", value = "referee-leader", description = "레프리 리더 조건 검색"),
            @ExampleObject(name = "ROLE_REFEREE", value = "referee", description = "레프리조건검색"),
            @ExampleObject(name = "ROLE_TABLE_OFFICIAL_LEADER", value = "table-official-leader", description = "테이블 오피셜 리더 조건검색"),
            @ExampleObject(name = "ROLE_TABLE_OFFICIAL", value = "table-official", description = " 테이블 오피셜 조건 검색")})
    String permissionsStr);


    @Operation(summary = "갤러리 관리자용 목록 조회", description = "갤러리 목록에 보여줄 대표이미지 url과 갤러리 ID 갤러리제목들을 반환<br>" +
            "관리자용으로 삭제된 갤러리도 조회됩니다. keyword 값이 있을시 검색된 데이터들을 반환하고 없을시 전체 목록 반환해줍니다.<br> " +
            "⬇️ex⬇️ 검색어나 검색기준이 null일시 쿼리파라미터에 명시적으로 표시하지 않아도 null로 처리됩니다<br>" +
            "/v1/api/post/manage?page=0&size=20&searchCriteriaString=null <br>" +
            "이렇게 요청 보내지 않고 /v1/api/post/manage?page=0&size=20 이렇게만 해도 됩니다.")
    @ApiResponse(responseCode = "400", description = "지원되지 않는 검색 조건",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "검색실패",
                                    description = "검색어를 담아 검색 요청을 보냈는데 검색조건이 이상할때 발생",
                                    value = """
                                            {
                                              "code": 400,
                                              "message": "BAD_REQUEST",
                                              "detailMessage": "지원되지않는 검색조건",
                                              "request": "개발자집주소"
                                            }""")
                    })
    )
    @ApiResponse(responseCode = "400", description = "갤러리 아이디 검색 요청에 숫자가 아닌 값이 들어옴",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "갤러리 아이디 검색 요청 실패",
                                    description = "갤러리 아이디 검색 요청에 숫자가 아닌 값이 들어옴",
                                    value = """
                                            {
                                               "code": 400,
                                               "message": "BAD_REQUEST",
                                               "detailMessage": "아이디 검색은 검색어가 숫자여야 합니다.",
                                               "request": "asdf"
                                             }""")
                    })
    )
    ResponseDto getManageGalleryList(@Parameter(description = "페이지 쪽수 (기본값 = 0)") int page,
                                     @Parameter(description = "페이지당 보여질 갤러리게시물 갯수 (기본값 = 6)") int size,
                                     @Parameter(description = "검색 키워드 null 일시 일반 목록 조회.") String keyword,
                                     @Parameter(description = "검색 기준 (기본값 = null) 검색어가 있을시 검색 기준은 필수 입니다.", examples = {
                                             @ExampleObject(name = "전체", value = "null", description = "전체 조회"),
                                             @ExampleObject(name = "이메일", value = "email", description = "이메일로 검색"),
                                             @ExampleObject(name = "제목", value = "title", description = "제목으로 검색"),
                                             @ExampleObject(name = "갤러리아이디", value = "id", description = "갤러리 고유번호로 검색")}) String searchCriteriaString,
                                     @Parameter(description = "조회권한 스탭갤러리, 일반갤러리 유무 아무값도 안넣을 시 전체 조회됩니다.", examples = {
                                             @ExampleObject(name = "공식갤러리", value = "true", description = "공식갤러리임"),
                                             @ExampleObject(name = "안공식갤러리", value = "false", description = "일반갤러리임"),
                                             @ExampleObject(name = "전체", value = "null", description = "전체 조회")})
                                     Boolean official,
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

    @Operation(summary = "게시물 관리자용 목록 조회", description = "게시물 목록에 보여줄 썸네일url과 게시물 제목, 내용, 첨부파일목록 반환<br>" +
            "관리자용으로 삭제된 게시물도 조회됩니다. keyword 값이 있을시 검색된 데이터들을 반환하고 없을시 전체 목록 반환해줍니다.<br> " +
            "⬇️ex⬇️ 검색어나 검색기준이 null일시 쿼리파라미터에 명시적으로 표시하지 않아도 null로 처리됩니다<br>" +
            "/v1/api/gallery/manage?page=0&size=20&searchCriteriaString=null <br>" +
            "이렇게 요청 보내지 않고 /v1/api/gallery/manage?page=0&size=20 이렇게만 해도 됩니다.")

    @ApiResponse(responseCode = "400", description = "갤러리 아이디 검색 요청에 숫자가 아닌 값이 들어옴",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "갤러리 아이디 검색 요청 실패",
                                    description = "갤러리 아이디 검색 요청에 숫자가 아닌 값이 들어옴",
                                    value = """
                                            {
                                               "code": 400,
                                               "message": "BAD_REQUEST",
                                               "detailMessage": "아이디 검색은 검색어가 숫자여야 합니다.",
                                               "request": "asdf"
                                             }""")
                    })
    )
    @GetMapping("/manage")
    ResponseDto getManagePostsList(@Parameter(description = "페이지 쪽수 (기본값 = 0)") int page,
                                   @Parameter(description = "페이지당 보여질 갤러리게시물 갯수 (기본값 = 20)") int size,
                                   @Parameter(description = "검색 키워드 null 일시 일반 목록 조회.") String keyword,
                                   @Parameter(description = "검색 기준 (기본값 = null) 검색어가 있을시 검색 기준은 필수 입니다.", examples = {
                                           @ExampleObject(name = "전체", value = "null", description = "전체 조회"),
                                           @ExampleObject(name = "이메일", value = "email", description = "이메일로 검색"),
                                           @ExampleObject(name = "제목", value = "title", description = "제목으로 검색"),
                                           @ExampleObject(name = "게시물아이디", value = "id", description = "게시물 고유번호로 검색"),
                                           @ExampleObject(name = "내용", value = "content", description = " 내용으로 검색")}) String searchCriteriaString,
                                   @Parameter(description = "카테고리 ex) notice, library, news", examples = {
                                           @ExampleObject(name = "전체", value = "null", description = "전체 조회 (null 값)"),
                                           @ExampleObject(name = "공지", value = "notice", description = "공지사항 카테고리"),
                                           @ExampleObject(name = "뉴스", value = "news", description = "뉴스 카테고리"),
                                           @ExampleObject(name = "라이브러리", value = "library", description = "자료실? 카테고리")})
                                   String category,
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
