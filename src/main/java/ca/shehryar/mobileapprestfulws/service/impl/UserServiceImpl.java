package ca.shehryar.mobileapprestfulws.service.impl;

import ca.shehryar.mobileapprestfulws.UserRepository;
import ca.shehryar.mobileapprestfulws.io.entity.UserEntity;
import ca.shehryar.mobileapprestfulws.service.UserService;
import ca.shehryar.mobileapprestfulws.shared.Utils;
import ca.shehryar.mobileapprestfulws.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Record already exists");
        }

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        String generatedUserId = utils.generateUserId(30);
        userEntity.setUserId(generatedUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        UserEntity storedDetails = userRepository.save(userEntity);

        UserDto returnVal = new UserDto();
        BeanUtils.copyProperties(storedDetails, returnVal);

        return returnVal;
    }
}
