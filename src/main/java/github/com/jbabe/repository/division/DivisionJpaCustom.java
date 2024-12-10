package github.com.jbabe.repository.division;

import java.util.Optional;

public interface DivisionJpaCustom {
    Optional<Integer> getDivisionIdByDivisionName(String divisionName);
}
