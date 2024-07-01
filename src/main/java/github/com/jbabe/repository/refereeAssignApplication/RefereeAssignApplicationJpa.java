package github.com.jbabe.repository.refereeAssignApplication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefereeAssignApplicationJpa extends JpaRepository<RefereeAssignApplication, Integer> {
}
