package github.com.jbabe.web.dto.infinitescrolling.criteria;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchCriteria {
    LATEST("최신"),
    POPULAR("인기"),
    NAME("이름"),
    USER("유저"),
    ACTIVITIES("활동");
    private final String value;


    public static SearchCriteria setValue(String criteria){
        try {
            return criteria != null ? SearchCriteria.valueOf(criteria.toUpperCase()) : SearchCriteria.LATEST;
        } catch (IllegalArgumentException e) {
            return LATEST;
        }
    }

}