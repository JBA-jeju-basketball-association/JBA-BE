package github.com.jbabe.service.mapper.converter;

import github.com.jbabe.web.dto.competition.CompetitionAdminListRequest;
import org.springframework.stereotype.Component;

@Component
public class SituationParamConverter extends EnumParamConverter<CompetitionAdminListRequest.Situation> {
    public SituationParamConverter() {
        super(CompetitionAdminListRequest.Situation.class);
    }
}
