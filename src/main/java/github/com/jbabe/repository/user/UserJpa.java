package github.com.jbabe.repository.user;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserJpa extends JpaRepository<User, Integer>, UserCustomDao {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByNameAndPhoneNum(String name, String phoneNum);

    boolean existsByEmailAndName(String email, String name);

    User findBySocialId(Long socialId);

}
