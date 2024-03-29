package github.com.jbabe.repository.division;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DivisionJpa extends JpaRepository<Division, Integer> {

}
