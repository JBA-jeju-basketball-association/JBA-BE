package github.com.jbabe.service.mapper.converter;

import github.com.jbabe.web.dto.competition.CompetitionAdminListRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
public abstract class EnumParamConverter<T extends PutJsonValue> implements Converter<String, T> {
    private final Class<T> enumType;
    @Override
    public T convert(@NonNull String source) {
        return T.fromValue(enumType,source);
    }
}
