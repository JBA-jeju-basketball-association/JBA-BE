package github.com.jbabe.repository.division;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DivisionEnumJpa extends JpaRepository<DivisionEnum, Long> {
    Optional<DivisionEnum> findByDivisionName(String divisionName);

}
