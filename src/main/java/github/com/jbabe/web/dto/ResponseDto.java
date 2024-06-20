package github.com.jbabe.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ResponseDto {
    @Schema(description = "응답 코드", example = "200")
    private int code;
    @Schema(description = "응답 메세지", example = "OK")
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "응답 데이터", example = "데이터는 test로 확인")
    private Object data;

    // 생성자, 게터 및 세터는 필요에 따라 추가할 수 있습니다.

    public ResponseDto() {
        this.code = HttpStatus.OK.value();
        this.message = HttpStatus.OK.name();
    }


    public ResponseDto(HttpStatus httpStatus) {
        this.code = httpStatus.value();
        this.message = httpStatus.name();
    }

    public ResponseDto(Object data) {
        this.code = HttpStatus.OK.value();
        this.message = HttpStatus.OK.name();
        this.data = data;
    }

}
