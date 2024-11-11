package github.com.jbabe.repository.assignmentorder;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentOrderRepository extends JpaRepository<AssignmentOrder, Long>, AssignmentOrderRepositoryCustom {
}
