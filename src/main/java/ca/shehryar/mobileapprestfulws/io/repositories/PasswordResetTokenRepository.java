package ca.shehryar.mobileapprestfulws.io.repositories;

import ca.shehryar.mobileapprestfulws.io.entity.PasswordResetTokenEntity;
import org.springframework.data.repository.CrudRepository;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {
    PasswordResetTokenEntity findByToken(String token);
}
