package github.com.jbabe.web.advice.errorResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "공통 에러 응답 객체")
public class ErrorResponse {
    private Integer code;
    private String message;
    private String detailMessage;
    private Object request;
}

