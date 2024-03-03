package github.com.jbabe.repository.scheduleAttachedFile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleAttachedFileJpa extends JpaRepository<ScheduleAttachedFile, Integer> {
}
