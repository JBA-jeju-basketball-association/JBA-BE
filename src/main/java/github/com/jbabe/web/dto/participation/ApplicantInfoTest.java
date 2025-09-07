package github.com.jbabe.web.dto.participation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantInfoTest {

    private Integer userId;
    private String name;
    private String phoneNum;
    private Integer failureCount;
}
