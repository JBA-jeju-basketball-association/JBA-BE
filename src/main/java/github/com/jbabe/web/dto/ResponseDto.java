package github.com.jbabe.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ResponseDto {
    private int code;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;

    // 생성자, 게터 및 세터는 필요에 따라 추가할 수 있습니다.

    public ResponseDto() {
        this.code = HttpStatus.OK.value();
        this.message = HttpStatus.OK.name();
    }

    public ResponseDto(Object data) {
        this.code = HttpStatus.OK.value();
        this.message = HttpStatus.OK.name();
        this.data = data;
    }
}
