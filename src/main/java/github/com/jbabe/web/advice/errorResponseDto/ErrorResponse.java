package github.com.jbabe.web.advice.errorResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "���� ���� ���� ��ü")
public class ErrorResponse {
    private Integer code;
    private String message;
    private String detailMessage;
    private Object request;
}

