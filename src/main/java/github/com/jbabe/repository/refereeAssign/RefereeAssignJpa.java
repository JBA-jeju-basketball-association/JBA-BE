package github.com.jbabe.repository.refereeAssign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefereeAssignJpa extends JpaRepository<RefereeAssign, Integer> {
}
