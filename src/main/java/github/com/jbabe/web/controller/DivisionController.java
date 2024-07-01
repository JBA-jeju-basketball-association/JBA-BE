package github.com.jbabe.web.controller;

import github.com.jbabe.service.DivisionEnumService;
import github.com.jbabe.web.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/api/division")
@RestController
@RequiredArgsConstructor
public class DivisionController {

    private final DivisionEnumService divisionEnumService;

    @GetMapping("/a-valid")
    public ResponseDto getAValidAllDivision() {
        return new ResponseDto(divisionEnumService.getAValidAllDivision());
    }

    @PostMapping("/a-valid")
    public ResponseDto createDivision(@RequestParam String divisionValue) {
        return new ResponseDto(divisionEnumService.createDivision(divisionValue));
    }
    @DeleteMapping("/a-valid")
    public ResponseDto deleteDivision(@RequestParam long id){
        return new ResponseDto(divisionEnumService.deleteDivision(id));
    }

}
