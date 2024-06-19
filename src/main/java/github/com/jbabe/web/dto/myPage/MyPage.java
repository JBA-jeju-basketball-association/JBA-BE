package github.com.jbabe.web.dto.myPage;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonSerialize(using = MyPageSerializer.class)
public class MyPage<T> {
    private Class<T> type;
    private List<T> content;
    private long totalPages;
    private long totalElements;
}
