package github.com.jbabe.web.dto.competition;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class PostResultRequest {
    @NotNull(message = "종별을 입력해주세요.")
    @NotBlank(message = "종별을 입력해주세요.")
    @Schema(description = "종별", example = "element")
    private String division;

    @Valid
    @NotNull(message = "결과를 입력해주세요")
    private List<PostResultRequestRow> postResultRequestRows;
}
