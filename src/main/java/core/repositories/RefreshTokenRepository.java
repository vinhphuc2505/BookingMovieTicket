package core.repositories;

import core.entities.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
