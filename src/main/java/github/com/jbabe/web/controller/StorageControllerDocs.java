package github.com.jbabe.web.controller;

import github.com.jbabe.web.dto.ResponseDto;
import github.com.jbabe.web.dto.awsTest2.SaveFileType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Tag(name = "AWS Storage", description = "AWS 버킷 관련 API (파일 업로드, 취소, 수정)")
public interface StorageControllerDocs {

    @ApiResponse(responseCode = "200",description = "파일 업로드 성공",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "파일 업로드 성공 후 반환값 예",
                                    description = "⬆️⬆️ data 안에 제목과 그리고 첨부된 이미지들을 files 배열로 목록들을 반환해줍니다. ",
                                    value = "{\n" +
                                            "    \"code\": 200,\n" +
                                            "    \"message\": \"OK\",\n" +
                                            "    \"data\": [\n" +
                                            "        {\n" +
                                            "            \"fileName\": \"스크린샷 2024-04-02 214224.png\",\n" +
                                            "            \"fileUrl\": \"https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/46ad7a78-3c71-494f-8b10-c422cb7ae236.png\"\n" +
                                            "        },\n" +
                                            "        {\n" +
                                            "            \"fileName\": \"스크린샷 2023-11-04 203440.png\",\n" +
                                            "            \"fileUrl\": \"https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/9de16ff9-fe3c-4bbb-b9c4-850791d65877.png\"\n" +
                                            "        }\n" +
                                            "    ]\n" +
                                            "}")
                    })
    )
    @Operation(summary = "파일 여러개 업로드", description = "멀티파트 요청으로 파일 여러개 한번에 업로드 후 <br>업로드된 정보 반환")
    ResponseDto uploadMultipleFiles(
            @Parameter(
                    description = "multipart/form-data 형식의 첨부파일 리스트를 input으로 받습니다. key 값은 uploadFiles 입니다<br>" +
                            "참고 : https://www.notion.so/API-b5441305b95c4d7bb5f456a95d93794e?p=b28c8491f090456cb9a95d144a8e6fc7&pm=s"

            )
            List<MultipartFile> multipartFiles,
            @Parameter(description = "첨부파일이 일반파일인지 대용량인지 기본값은 small  ex) small, large", examples = {
                    @ExampleObject(name = "일반첨부", value = "small", description = "일반 파일"),
                    @ExampleObject(name = "대용량첨부", value = "large", description = "대용량 파일")
            }) Optional<SaveFileType> type
    ) ;


    @ApiResponse(responseCode = "200",description = "파일 여러개 삭제 성공",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "삭제 성공 응답 예",
                                    description = "⬆️⬆️ 성공! (이미 삭제된 파일의 url 또는 존재하지 않는 url로 시도해도 에러는 안뜹니다. 삭제에 실패했을경우에만 에러발생)",
                                    value = "{\n" +
                                            "  \"code\": 200,\n" +
                                            "  \"message\": \"OK\"\n" +
                                            "}")
                    })
    )
    @Operation(summary = "파일 여러개 삭제", description = "업로드 취소 요청(버킷에서 해당 파일 삭제)")
    ResponseDto deleteMultipleFiles(
            @Parameter(
                    description = "삭제할 파일의 URL들",
                    examples = @ExampleObject(
                            name = "예시",description = "삭제 원하는 파일의 URL을 파라미터로 전달",
                            value = "[\"http://example.com/file1.jpg\", \"http://example.com/file2.jpg\"]"
                    )
            )
            List<String> fileUrls ) ;

    @ApiResponse(responseCode = "200",description = "파일 업로드 성공",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "파일 업로드 성공 후 반환값 예",
                                    description = "⬆️⬆️ data 안에 제목과 그리고 첨부된 이미지들을 files 배열로 목록들을 반환해줍니다. ",
                                    value = "{\n" +
                                            "    \"code\": 200,\n" +
                                            "    \"message\": \"OK\",\n" +
                                            "    \"data\": [\n" +
                                            "        {\n" +
                                            "            \"fileName\": \"스크린샷 2024-04-02 214224.png\",\n" +
                                            "            \"fileUrl\": \"https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/46ad7a78-3c71-494f-8b10-c422cb7ae236.png\"\n" +
                                            "        },\n" +
                                            "        {\n" +
                                            "            \"fileName\": \"스크린샷 2023-11-04 203440.png\",\n" +
                                            "            \"fileUrl\": \"https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/9de16ff9-fe3c-4bbb-b9c4-850791d65877.png\"\n" +
                                            "        }\n" +
                                            "    ]\n" +
                                            "}")
                    })
    )
    @Operation(summary = "업로드된 파일 수정", description = "업로드된 파일 수정 <br>(삭제할 URL들을 받아서 버킷에서 삭제한후 새로 업로드된 파일은 업로드 후 파일명과 url을 새로 반환)")
     ResponseDto modifyMultipleFiles(
             @Parameter(
                     description = "삭제할 파일의 URL들",
                     examples = @ExampleObject(
                             name = "예시",description = "삭제 원하는 파일의 URL을 파라미터로 전달",
                             value = "[\"http://example.com/file1.jpg\", \"http://example.com/file2.jpg\"]"
                     )
             )
             List<String> deleteFileUrls,
             @Parameter(
                     description = "multipart/form-data 형식의 첨부파일 리스트를 input으로 받습니다. key 값은 uploadFiles 입니다<br>" +
                             "참고 : https://www.notion.so/API-b5441305b95c4d7bb5f456a95d93794e?p=b28c8491f090456cb9a95d144a8e6fc7&pm=s"

             )
             List<MultipartFile> multipartFiles,
             @Parameter(description = "첨부파일이 일반파일인지 대용량인지 기본값은 small  ex) small, large", examples = {
                     @ExampleObject(name = "일반첨부", value = "small", description = "일반 파일"),
                     @ExampleObject(name = "대용량첨부", value = "large", description = "대용량 파일")
             }) Optional<SaveFileType> type);
}
