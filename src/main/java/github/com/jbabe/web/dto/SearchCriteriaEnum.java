package github.com.jbabe.web.dto;

import github.com.jbabe.service.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchCriteriaEnum {
    TITLE("title"),
    EMAIL("email"),
    CONTENT("content"),
    ID("id");

    private final String value;


    public static SearchCriteriaEnum fromValue(String value) {
        for (SearchCriteriaEnum searchCriteriaEnum : SearchCriteriaEnum.values()) {
            if (searchCriteriaEnum.getValue().equals(value)) return searchCriteriaEnum;
        }
        if(value == null) throw new BadRequestException("검색어가 있을때 검색 조건은 필수 입니다.", null);
        else throw new BadRequestException("지원되지않는 검색조건", value);
    }

}
