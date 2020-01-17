package ca.shehryar.mobileapprestfulws.service.impl;

import ca.shehryar.mobileapprestfulws.io.entity.AddressEntity;
import ca.shehryar.mobileapprestfulws.io.entity.UserEntity;
import ca.shehryar.mobileapprestfulws.io.repositories.AddressRepository;
import ca.shehryar.mobileapprestfulws.io.repositories.UserRepository;
import ca.shehryar.mobileapprestfulws.service.AddressService;
import ca.shehryar.mobileapprestfulws.shared.dto.AddressDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDto> getAddresses(String userId) {
        List<AddressDto> returnVal = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            return returnVal;
        }

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);

        for (AddressEntity addressEntity : addresses) {
            returnVal.add(modelMapper.map(addressEntity, AddressDto.class));
        }

        return returnVal;
    }

    @Override
    public AddressDto getAddress(String addressId) {
        AddressDto returnVal = null;

        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);

        if (addressEntity != null) {
            returnVal = new ModelMapper().map(addressEntity, AddressDto.class);
        }

        return returnVal;
    }
}
