package github.com.jbabe.service.mapper;

import github.com.jbabe.repository.division.DivisionEnum;
import github.com.jbabe.web.dto.division.DivisionEnumDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DivisionMapper {
    DivisionMapper INSTANCE = Mappers.getMapper(DivisionMapper.class);

    @Mapping(target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    DivisionEnumDto DivisionEnumToDivisionEnumDto(DivisionEnum divisionEnum);

}
