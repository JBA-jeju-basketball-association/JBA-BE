package github.com.jbabe.service;

import github.com.jbabe.repository.division.DivisionEnum;
import github.com.jbabe.repository.division.DivisionEnumJpa;
import github.com.jbabe.service.exception.ConflictException;
import github.com.jbabe.service.exception.NotAcceptableException;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.mapper.DivisionMapper;
import github.com.jbabe.web.dto.division.DivisionEnumDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DivisionEnumService {

    private final DivisionEnumJpa divisionEnumJpa;

    public List<DivisionEnumDto> getAValidAllDivision() {
        return divisionEnumJpa.findAll().stream().map(DivisionMapper.INSTANCE::DivisionEnumToDivisionEnumDto).toList();
    }
    @Transactional
    public DivisionEnumDto createDivision(String divisionValue) {
        try {
            return DivisionMapper.INSTANCE.DivisionEnumToDivisionEnumDto(divisionEnumJpa.save(new DivisionEnum(divisionValue)));
        }catch (DataIntegrityViolationException e) {
            throw new ConflictException("Division already exists.", divisionValue);
        }catch (Exception e) {
            throw new NotAcceptableException(e.getMessage(), divisionValue);
        }
    }
    @Transactional
    public String deleteDivision(long id) {
        DivisionEnum itemsToBeDeleted = divisionEnumJpa.findById(id).orElseThrow(() -> new NotFoundException("Division not found.", id));
        try {
            divisionEnumJpa.delete(itemsToBeDeleted);
            return "Division deleted successfully. "+itemsToBeDeleted.getDivisionName();
        }catch (Exception e) {
            throw new NotAcceptableException(e.getMessage(), id);
        }
    }
}
