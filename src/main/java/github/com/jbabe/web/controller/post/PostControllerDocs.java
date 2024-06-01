package github.com.jbabe.web.controller.post;

import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import github.com.jbabe.web.dto.post.PostReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Tag(name = "Post", description = "게시물 관련 API")
public interface PostControllerDocs {
    @Operation(summary = "게시물 목록 조회", description = "각 카테고리별 게시물 목록을 공지게시글과 일반게시글을 반환")
    @ApiResponse(responseCode = "200",description = "게시물 목록 조회 성공",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "페이지 조회 성공 예",
                                    description = "⬆️⬆️ data 안에 posts 배열로 목록들을 반환해주고<br> totalPosts는 총 게시물, totalPages는 총 페이지수 입니다. ",
                                    value = "{\n" +
                                            "  \"code\": 200,\n" +
                                            "  \"message\": \"OK\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"posts\": [\n" +
                                            "      \"~~게시물 목록 배열들~~ 생략\"\n" +
                                            "    ],\n" +
                                            "    \"totalPosts\": 10,\n" +
                                            "    \"totalPages\": 1\n" +
                                            "  }\n" +
                                            "}")
                    })
    )
    @ApiResponse(responseCode = "404", description = "존재하지 않는 페이지 (totalPages를 넘어가는 page로 요청한 경우)",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "페이지 조회 실패 예제",
                                    description = "⬆️⬆️ 총 페이지 수 보다 더 큰 숫자의 페이지를 요청한 경우 발생되는 익셉션 입니다.<br>" +
                                            "request에는 요청들어온 숫자가 반환됩니다.<br>" +
                                            "예제에서는 99페이지를 요청했고 99페이지는 존재 하지않아 에러가 발생된 상황.",
                                    value = "{\n" +
                                            "  \"code\": 404,\n" +
                                            "  \"message\": \"NOT_FOUND\",\n" +
                                            "  \"detailMessage\": \"Page Not Found\",\n" +
                                            "  \"request\": 99\n" +
                                            "}")
                    })
    )
    ResponseDto getAllPostsList(
            @Parameter(description = "페이지 쪽수 (기본값 = 0)") int page,
            @Parameter(description = "페이지당 공지를 제외한 일반게시글 갯수 (기본값 = 10)") int size,
            @Parameter(description = "카테고리 ex) notice, library, news", examples = {
                    @ExampleObject(name = "공지", value = "notice", description = "공지사항 카테고리"),
                    @ExampleObject(name = "뉴스", value = "news", description = "뉴스 카테고리"),
                    @ExampleObject(name = "라이브러리", value = "library", description = "자료실? 카테고리")})
            String category);


    @Operation(summary = "게시물 상세 조회", description = "원하는 게시물 조회")
    @ApiResponse(responseCode = "200",description = "게시물 조회 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 페이지 (해당 카테고리의 해당 게시물번호는 존재 하지않음)",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "게시물 조회 실패 예제",
                                    description = "⬆️⬆️ 존재 하지 않는 postId로 요청이 들어온 경우",
                                    value = "{\n" +
                                            "  \"code\": 404,\n" +
                                            "  \"message\": \"NOT_FOUND\",\n" +
                                            "  \"detailMessage\": \"Post Not Found\",\n" +
                                            "  \"request\": 99\n" +
                                            "}")
                    })
    )
    ResponseDto getPostDetails(
            @Parameter(description = "카테고리 ex) notice, library, news", examples = {
                    @ExampleObject(name = "공지", value = "notice", description = "공지사항 카테고리"),
                    @ExampleObject(name = "뉴스", value = "news", description = "뉴스 카테고리"),
                    @ExampleObject(name = "라이브러리", value = "library", description = "자료실? 카테고리")})
            String category,
            @Parameter(description = "게시물 고유 번호", example = "58")
            Integer postId);



    @Operation(summary = "게시물 작성", description = "게시물 작성 첨부파일은 key값을 uploadFiles로 게시물 내용은 key값 body로 둘다 form-data에 담아요청<br>content 부분에 삽입된 이미지들은 올릴때 업로드 api로 바로 업로드 되구<br>반환된 파일명과 url을 files에 담아 요청 보내주시면 됩니다!")
    @ApiResponse(responseCode = "404", description = "작성자 미상",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "작성자 미상",
                                    description = "⬆️⬆️ 로그인된 작성자의 정보를 찾을 수 없음 (웬만해선 뜰일 없음)",
                                    value = "{\n" +
                                            "  \"code\": 404,\n" +
                                            "  \"message\": \"NOT_FOUND\",\n" +
                                            "  \"detailMessage\": \"User Not Found\",\n" +
                                            "  \"request\": \"abc@abc.com\"\n" +
                                            "}")
                    })
    )
    @ApiResponse(responseCode = "400", description = "DB 반영 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "DB 반영 실패",
                                    description = "⬆️⬆️ DB에 반영하는데 실패함, DB 조건에 맞지 않는 정보로 인해 DB 세이브 실패함<br>ex) 중복 불가 정보에 중복된 값이 들어온 경우",
                                    value = "{\n" +
                                            "  \"code\": 400,\n" +
                                            "  \"message\": \"BAD_REQUEST\",\n" +
                                            "  \"detailMessage\": \"DB에 반영하는데 실패하였습니다. (제목이 중복됐을 가능성이 있습니다.)  could not execute statement [(conn=271638) Duplicate entry '게시물 입니다3.' for key 'name'] [insert into post (category,content,create_at,is_announcement,name,user_id) values (?,?,?,?,?,?)]; SQL [insert into post (category,content,create_at,is_announcement,name,user_id) values (?,?,?,?,?,?)]; constraint [name]\",\n" +
                                            "  \"request\": \"게시물 입니다3\"\n" +
                                            "}")
                    })
    )
    @ApiResponse(responseCode = "200",description = "게시물 작성 성공",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "게시물 작성 성공 예",
                                    description = "⬆️⬆️ 성공!",
                                    value = "{\n" +
                                            "  \"code\": 200,\n" +
                                            "  \"message\": \"OK\"\n" +
                                            "}")
                    })
    )
    ResponseDto regPost(
            @Parameter(description = "카테고리 ex) notice, library, news", examples = {
                    @ExampleObject(name = "공지", value = "notice", description = "공지사항 카테고리"),
                    @ExampleObject(name = "뉴스", value = "news", description = "뉴스 카테고리"),
                    @ExampleObject(name = "라이브러리", value = "library", description = "자료실? 카테고리")})
           String category,
            @Parameter(description = "공지 여부 기본값은 false (공지일시 페이지 목록 상단에 고정됨)", examples = {
                    @ExampleObject(name = "공지", value = "true", description = "공지 (목록 상단 고정)"),
                    @ExampleObject(name = "안공지", value = "false", description = "공지 아님")})
            boolean isOfficial,PostReqDto postReqDto, List<MultipartFile> multipartFiles,
            @Parameter(description = "첨부파일이 일반파일인지 대용량인지 기본값은 small  ex) small, large", examples = {
                    @ExampleObject(name = "일반첨부", value = "small", description = "일반 파일"),
                    @ExampleObject(name = "대용량첨부", value = "large", description = "대용량 파일")
            })Optional<SaveFileType> type);

}
