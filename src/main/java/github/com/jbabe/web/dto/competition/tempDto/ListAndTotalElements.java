package github.com.jbabe.web.dto.competition.tempDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
public class ListAndTotalElements <T> {
    List<T> values;
    long totalElements;

    public static <T> ListAndTotalElements<T> of(List<T> values, long totalElements) {
        return new ListAndTotalElements<>(values, totalElements);
    }
}
