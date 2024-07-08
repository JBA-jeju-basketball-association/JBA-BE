package github.com.jbabe.service;

import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.web.dto.SearchCriteriaEnum;
import github.com.jbabe.web.dto.manageUser.UserSearchCriteriaEnum;

import java.time.LocalDate;

public class SearchQueryParamUtil {

    public static void validateAndAdjustDates(String keyword, SearchCriteriaEnum searchCriteria, LocalDate startDate, LocalDate endDate) {
        if(keyword!=null) validateKeyword(keyword, searchCriteria);
        validateDates(startDate, endDate);
    }

    private static void validateKeyword(String keyword, SearchCriteriaEnum searchCriteria) {
        if(!(searchCriteria.equals(SearchCriteriaEnum.ID)) && (keyword.isEmpty() || keyword.length() == 1)) throw new BadRequestException("검색어는 2글자 이상이어야 합니다.", keyword);
        if(searchCriteria.equals(SearchCriteriaEnum.ID) && !keyword.matches("^[0-9]*$")) {
            throw new BadRequestException("아이디 검색은 검색어가 숫자여야 합니다.", keyword);
        }
    }
    private static void validateDates(LocalDate startDate, LocalDate endDate) {
        if(startDate !=null&&startDate.isAfter(LocalDate.now())) throw new BadRequestException("시작일이 현재보다 늦을 수 없습니다.", "시작일 : "+startDate + ", 현재 : " + LocalDate.now());
        if(endDate!=null&&endDate.isAfter(LocalDate.now())) throw new BadRequestException("종료일이 현재보다 늦을 수 없습니다.", "종료일 : "+endDate+", 현재 : "+LocalDate.now());
        if(startDate!=null&&endDate!=null&&startDate.isAfter(endDate)) throw new BadRequestException("시작일이 종료일보다 늦을 수 없습니다.", "시작일 : "+startDate + ", 종료일 : " + endDate);
    }

    public static void userValidateKeywordAndAdjustDates(String keyword, UserSearchCriteriaEnum searchCriteria, LocalDate startDate, LocalDate endDate) {
        if(keyword!=null) userValidateKeyword(keyword, searchCriteria);
        validateDates(startDate, endDate);
    }
    private static void userValidateKeyword(String keyword, UserSearchCriteriaEnum searchCriteria) {
        if(!(searchCriteria.equals(UserSearchCriteriaEnum.USER_ID)) && (keyword.isEmpty() || keyword.length() == 1))throw new BadRequestException("검색어는 2글자 이상이어야 합니다.", keyword);
        if(searchCriteria.equals(UserSearchCriteriaEnum.USER_ID) && !keyword.matches("^[0-9]*$")) {
            throw new BadRequestException("아이디 검색은 검색어가 숫자여야 합니다.", keyword);
        }
    }

}
