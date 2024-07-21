package github.com.jbabe.web.dto.storage;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestFileDto {
    private Boolean uploaded;
    private String url;
    private String fileName;
}
