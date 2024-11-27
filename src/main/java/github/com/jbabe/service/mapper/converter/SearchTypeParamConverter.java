package github.com.jbabe.service.mapper.converter;

import github.com.jbabe.web.dto.competition.CompetitionAdminListRequest;
import org.springframework.stereotype.Component;

@Component
public class SearchTypeParamConverter extends EnumParamConverter<CompetitionAdminListRequest.SearchType> {
    public SearchTypeParamConverter() {
        super(CompetitionAdminListRequest.SearchType.class);
    }

}
