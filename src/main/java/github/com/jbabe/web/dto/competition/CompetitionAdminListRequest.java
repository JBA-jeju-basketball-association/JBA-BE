package github.com.jbabe.web.dto.competition;

import com.fasterxml.jackson.annotation.JsonCreator;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.mapper.converter.PutJsonValue;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class  CompetitionAdminListRequest{
    private SearchType searchType;
    private String stringKey;
    private Integer integerKey;
    private LocalDate filterStartDate;
    private LocalDate filterEndDate;
    private String division;
    private Situation situation;
    private int page;
    private int size;


    public static CompetitionAdminListRequest of(SearchType searchType, String searchKey, LocalDate filterStartDate, LocalDate filterEndDate, String division, Situation situation, int page, int size) {

        try {
            return switch (searchType) {
                case ID ->
                        new CompetitionAdminListRequest(searchType, null, Integer.parseInt(searchKey), filterStartDate, filterEndDate, division, situation, page, size);
                case TITLE ->
                        new CompetitionAdminListRequest(searchType, searchKey, null, filterStartDate, filterEndDate, division, situation, page, size);
                case EMAIL -> {
                    String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
                    if (searchKey!=null && searchKey.matches(emailRegex)) {
                        yield new CompetitionAdminListRequest(searchType, searchKey, null, filterStartDate, filterEndDate, division, situation, page, size);
                    } else {
                        throw new IllegalArgumentException("이메일 조건을 선택했지만 키워드가 이메일 형식이 아닙니다.");
                    }
                }
            };
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage(), Map.of( "searchType", searchType,"searchKey", searchKey==null ? "null":searchKey));
        }
    }

    @Getter
    @AllArgsConstructor
    public enum SearchType implements PutJsonValue {
        TITLE("title"), EMAIL("email"), ID("id");
        private final String value;
    }
    @Getter
    @AllArgsConstructor
    public enum Situation implements PutJsonValue {
        ALL("전체"), PROGRESS("진행중"), END("종료"), READY("예정");
        private final String value;

    }

}
