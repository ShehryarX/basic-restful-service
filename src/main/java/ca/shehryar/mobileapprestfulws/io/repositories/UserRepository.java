package ca.shehryar.mobileapprestfulws.io.repositories;

import ca.shehryar.mobileapprestfulws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
}