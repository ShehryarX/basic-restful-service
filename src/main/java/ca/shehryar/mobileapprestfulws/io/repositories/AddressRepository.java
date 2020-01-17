package ca.shehryar.mobileapprestfulws.io.repositories;

import ca.shehryar.mobileapprestfulws.io.entity.AddressEntity;
import ca.shehryar.mobileapprestfulws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
    List<AddressEntity> findAllByUserDetails(UserEntity user);
    AddressEntity findByAddressId(String addressId);
}
