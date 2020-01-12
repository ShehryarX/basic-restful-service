package ca.shehryar.mobileapprestfulws;

import ca.shehryar.mobileapprestfulws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
}
