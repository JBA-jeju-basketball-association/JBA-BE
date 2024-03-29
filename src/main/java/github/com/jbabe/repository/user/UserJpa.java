package github.com.jbabe.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpa extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);

    boolean existsByPhoneNum(String phoneNum);
    Optional<User> findByEmail(String email);
}
