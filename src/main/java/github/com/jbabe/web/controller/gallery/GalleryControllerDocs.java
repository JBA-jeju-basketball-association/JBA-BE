package github.com.jbabe.web.controller.gallery;

import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.gallery.GalleryDetailsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Gallery", description = "갤러리 관련 API")
public interface GalleryControllerDocs {

    @Operation(summary = "갤러리 목록 조회", description = "갤러리 목록에 보여줄 대표이미지 url과 갤러리 ID 갤러리제목들을 반환")
    @ApiResponse(responseCode = "200",description = "게시물 목록 조회 성공",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "갤러리 목록 조회 성공 예",
                                    description = "⬆️⬆️ data 안에 galleries 배열로 목록들을 반환해주고<br> totalPosts는 총 게시물, totalPages는 총 페이지수 입니다. ",
                                    value = "{\n" +
                                            "  \"code\": 200,\n" +
                                            "  \"message\": \"OK\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"totalPages\": 13,\n" +
                                            "    \"totalGalleries\": 75,\n" +
                                            "    \"galleries\": [\n" +
                                            "      {\n" +
                                            "        \"galleryId\": 1,\n" +
                                            "        \"title\": \"[2024] 제주특별자치도 도민체전\",\n" +
                                            "        \"fileName\": \"1.png\",\n" +
                                            "        \"imgUrl\": \"https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/c8db15cc-a145-4aa1-869a-e3e650b3fcf9.png\"\n" +
                                            "      },\n" +
                                            "      \"....~~ 이외 목록 생략 \" \n" +
                                            "    ]\n" +
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
    ResponseDto getGalleryList(
            @Parameter(description = "페이지 쪽수 (기본값 = 0)") int page,
            @Parameter(description = "페이지당 보여질 갤러리게시물 갯수 (기본값 = 6)") int size,
            @Parameter(description = "공식 갤러리 인지 일반사진 갤러리 인지", examples = {
                    @ExampleObject(name = "공식갤러리", value = "true", description = "공식갤러리임"),
                    @ExampleObject(name = "안공식갤러리", value = "false", description = "일반갤러리임")})
            boolean official);

    @ApiResponse(responseCode = "200",description = "갤러리 상세 조회 성공",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "갤러리 상세 조회 성공 예",
                                    description = "⬆️⬆️ data 안에 제목과 그리고 첨부된 이미지들을 files 배열로 목록들을 반환해줍니다. ",
                                    value = "{\n" +
                                            "  \"code\": 200,\n" +
                                            "  \"message\": \"OK\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"title\": \"23-24 챔피언스 리그 8강 1R\",\n" +
                                            "    \"files\": [\n" +
                                            "      {\n" +
                                            "        \"fileName\": \"2.png\",\n" +
                                            "        \"fileUrl\": \"https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/930d3711-de9d-4f31-9e73-59a3242477c7.png\"\n" +
                                            "      },\n" +
                                            "      {\n" +
                                            "        \"fileName\": \"36.png\",\n" +
                                            "        \"fileUrl\": \"https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/930d3711-de9d-4f31-9e73-59a3242477c7.png\"\n" +
                                            "      },\n" +
                                            "      \"~....~ 이외에 파일들 생략\"\n" +
                                            "    ]\n" +
                                            "  }\n" +
                                            "}")
                    })
    )
    @ApiResponse(responseCode = "404", description = "존재하지 않는 갤러리 번호",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "갤러리 조회실패 ",
                                    description = "⬆️⬆️ 존재하지 않는 갤러리 번호로 조회요청이 온경우",
                                    value = "{\n" +
                                            "  \"code\": 404,\n" +
                                            "  \"message\": \"NOT_FOUND\",\n" +
                                            "  \"detailMessage\": \"Not Found Gallery\",\n" +
                                            "  \"request\": 600\n" +
                                            "}")
                    })
    )
    @Operation(summary = "갤러리 상세 조회", description = "갤러리Id로 갤러리 상세내용 조회")
    ResponseDto getGalleryPost(
            @Parameter(description = "상세 조회할 갤러리 고유번호", example = "2")
            int galleryId);

    @Operation(summary = "갤러리 게시물 작성", description = "갤러리 게시물 작성입니다.  첨부파일은 별도의 /multipart-files api를 사용해 업로드하고,<br>반환된 파일 name과 url을 담아서 요청 보내주셔야 해요!")
    @ApiResponse(responseCode = "200",description = "갤러리 작성 성공",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "갤러리 작성 성공 예",
                                    description = "⬆️⬆️ 성공!",
                                    value = "{\n" +
                                            "  \"code\": 200,\n" +
                                            "  \"message\": \"OK\"\n" +
                                            "}")
                    })
    )
    @ApiResponse(responseCode = "400", description = "DB 반영 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "DB 반영 실패",
                                    description = "⬆️⬆️ DB에 반영하는데 실패함, DB 조건에 맞지 않는 정보로 인해 DB 세이브 실패함",
                                    value = "{\n" +
                                            "  \"code\": 400,\n" +
                                            "  \"message\": \"BAD_REQUEST\",\n" +
                                            "  \"detailMessage\": \"DB에 반영하는데 실패하였습니다. (제목이 중복됐을 가능성이 있습니다.)  could not execute statement [(conn=271638) Duplicate entry '게시물 입니다3.' for key 'name'] [insert into post (category,content,create_at,is_announcement,name,user_id) values (?,?,?,?,?,?)]; SQL [insert into post (category,content,create_at,is_announcement,name,user_id) values (?,?,?,?,?,?)]; constraint [name]\",\n" +
                                            "  \"request\": \"갤러리 입니다3\"\n" +
                                            "}")
                    })
    )
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
    ResponseDto regGalleryPost(GalleryDetailsDto requestRegister,
                                     CustomUserDetails customUserDetails,
                                      @Parameter(description = "작성될 갤러리가 공식 갤러리 인지 일반사진 갤러리 인지", examples = {
                                              @ExampleObject(name = "공식갤러리", value = "true", description = "공식갤러리임"),
                                              @ExampleObject(name = "안공식갤러리", value = "false", description = "일반갤러리임")})
                                    boolean isOfficial);


}
