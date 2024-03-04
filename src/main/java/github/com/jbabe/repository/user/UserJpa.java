package github.com.jbabe.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpa extends JpaRepository<User, Integer> {

    @Query("SELECT u " +
            "FROM User u " +
            "join fetch u.userRoles ur " +
            "join fetch ur.role " +
            "where u.email = :email")
    Optional<User> findByEmailFetchJoin(String email);
}
