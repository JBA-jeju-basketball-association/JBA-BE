package github.com.jbabe.web.dto.manageUser;

import github.com.jbabe.service.exception.BadRequestException;
import lombok.AllArgsConstructor;

import java.util.EnumSet;

@AllArgsConstructor
public enum UserSearchCriteriaEnum {
    USER_ID("id"), USER_NAME("name"), USER_EMAIL("email"), USER_TEAM("team");
    private final String path;
    public static UserSearchCriteriaEnum pathToEnum(String path){
        for(UserSearchCriteriaEnum s: EnumSet.allOf(UserSearchCriteriaEnum.class)) if(s.path.equals(path)) return s;
        throw new BadRequestException("SearchCriteria Incorrectly Entered", path);
    }
}
