package core.repositories;

import core.entities.User;
import core.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByUsernameOrEmail(String username, String email);

    boolean existsByRole(UserRole role);

    @Query(value = "SELECT * FROM users u WHERE u.username = :loginName OR u.email = :loginName", nativeQuery = true)
    Optional<User> findByUsernameOrEmail(@Param("loginName") String loginName);
}
