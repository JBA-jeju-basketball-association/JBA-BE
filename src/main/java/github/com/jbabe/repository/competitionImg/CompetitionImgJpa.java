package github.com.jbabe.repository.competitionImg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionImgJpa extends JpaRepository<CompetitionImg, Integer> {

}
