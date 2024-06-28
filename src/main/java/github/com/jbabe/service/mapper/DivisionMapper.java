package github.com.jbabe.service.mapper;

import github.com.jbabe.repository.division.DivisionEnum;
import github.com.jbabe.web.dto.division.DivisionEnumDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DivisionMapper {
    DivisionMapper INSTANCE = Mappers.getMapper(DivisionMapper.class);

    DivisionEnumDto DivisionEnumToDivisionEnumDto(DivisionEnum divisionEnum);

}
