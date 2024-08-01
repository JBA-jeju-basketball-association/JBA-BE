package github.com.jbabe.repository.user;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserJpa extends JpaRepository<User, Integer>, UserCustomDao {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByNameAndPhoneNumAndDateOfBirth(String name, String phoneNum, LocalDate dateOfBirth);

    boolean existsByEmailAndDateOfBirthAndName(String email, LocalDate dateOfBirth, String name);
}
