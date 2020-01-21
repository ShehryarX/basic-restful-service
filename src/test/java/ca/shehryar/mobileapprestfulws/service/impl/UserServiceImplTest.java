package ca.shehryar.mobileapprestfulws.service.impl;

import ca.shehryar.mobileapprestfulws.exceptions.UserServiceException;
import ca.shehryar.mobileapprestfulws.io.entity.AddressEntity;
import ca.shehryar.mobileapprestfulws.io.entity.UserEntity;
import ca.shehryar.mobileapprestfulws.io.repositories.PasswordResetTokenRepository;
import ca.shehryar.mobileapprestfulws.io.repositories.UserRepository;
import ca.shehryar.mobileapprestfulws.shared.AmazonSES;
import ca.shehryar.mobileapprestfulws.shared.Utils;
import ca.shehryar.mobileapprestfulws.shared.dto.AddressDto;
import ca.shehryar.mobileapprestfulws.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    Utils utils;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    AmazonSES amazonSES;

    final String userId = "dhfiosuadhfwjkeh";
    final String encodedPassword = "asdjlkfhkadlsh";
    final UserEntity userEntity = new UserEntity();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userEntity.setId(1L);
        userEntity.setFirstName("Shehryar");
        userEntity.setLastName("Assad");
        userEntity.setUserId(userId);
        userEntity.setEmail("xshehryar@gmail.com");
        userEntity.setEmailVerificationToken("email_verif_token");
        userEntity.setEncryptedPassword(encodedPassword);
        userEntity.setAddresses(getAddressesEntity());
    }

    @Test
    void getUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = userService.getUser("xshehryar@gmail.com");

        assertNotNull(userDto);
        assertEquals(userEntity.getFirstName(), userDto.getFirstName());
    }

    @Test
    void getUser_UsernameNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.getUser("xshehryar@gmail.com"));
    }

    @Test
    void createUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("saodihfu923ysdf");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        Mockito.doNothing().when(amazonSES).verifyEmail(any(UserDto.class));

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName("Shehryar");
        userDto.setLastName("Assad");
        userDto.setPassword("my_password123");
        userDto.setEmail("xshehryar@gmail.com");

        UserDto storedUser = userService.createUser(userDto);

        assertNotNull(storedUser);
        assertEquals(userEntity.getFirstName(), storedUser.getFirstName());
        assertEquals(userEntity.getLastName(), storedUser.getLastName());
        assertNotNull(storedUser.getUserId());
        assertEquals(storedUser.getAddresses().size(), userEntity.getAddresses().size());
        verify(utils, times(2)).generateAddressId(30);
        verify(bCryptPasswordEncoder, times(1)).encode("my_password123");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void createUser_CreateUserServiceException() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName("Shehryar");
        userDto.setLastName("Assad");
        userDto.setPassword("my_password123");
        userDto.setEmail("xshehryar@gmail.com");

        assertThrows(UserServiceException.class, () -> userService.createUser(userDto));
    }

    private List<AddressDto> getAddressesDto() {
        AddressDto shippingAddressDto = new AddressDto();
        shippingAddressDto.setType("shipping");
        shippingAddressDto.setCity("Toronto");
        shippingAddressDto.setCity("Canada");
        shippingAddressDto.setPostalCode("M1M1M1");
        shippingAddressDto.setStreetName("123 Temp St");

        AddressDto mailingAddressDto = new AddressDto();
        mailingAddressDto.setType("shipping");
        mailingAddressDto.setCity("Toronto");
        mailingAddressDto.setCity("Canada");
        mailingAddressDto.setPostalCode("M1M1M1");
        mailingAddressDto.setStreetName("123 Temp St");

        List<AddressDto> addresses = new ArrayList<>();
        addresses.add(mailingAddressDto);
        addresses.add(mailingAddressDto);
        return addresses;
    }

    private List<AddressEntity> getAddressesEntity() {
        List<AddressDto> addresses = getAddressesDto();
        Type listType = new TypeToken<List<AddressEntity>>() {}.getType();
        return new ModelMapper().map(addresses, listType);
    }
}