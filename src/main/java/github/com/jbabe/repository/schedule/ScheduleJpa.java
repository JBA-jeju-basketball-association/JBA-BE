package github.com.jbabe.repository.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleJpa extends JpaRepository<Schedule, Integer> {
}
