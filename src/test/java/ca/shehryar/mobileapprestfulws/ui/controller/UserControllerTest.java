package ca.shehryar.mobileapprestfulws.ui.controller;

import ca.shehryar.mobileapprestfulws.service.UserService;
import ca.shehryar.mobileapprestfulws.shared.dto.UserDto;
import ca.shehryar.mobileapprestfulws.ui.model.response.UserRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserService userService;

    UserDto userDto;

    final String userId = "dfhsduifh";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        userDto = new UserDto();
        userDto.setFirstName("Shehryar");
        userDto.setLastName("Assad");
        userDto.setUserId(userId);
        userDto.setPassword("my_password123");
        userDto.setEncryptedPassword("encrypted_pw");
        userDto.setEmail("xshehryar@gmail.com");
        userDto.setEmailVerificationStatus(Boolean.FALSE);
        userDto.setEmailVerificationToken(null);
    }

    @Test
    void getUser() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);
        UserRest userRest = userController.getUser(userId);

        assertNotNull(userRest);
        assertEquals(userRest.getUserId(), userDto.getUserId());
        assertEquals(userRest.getFirstName(), userDto.getFirstName());
        assertEquals(userRest.getLastName(), userDto.getLastName());
        assertEquals(userDto.getAddresses().size(), userRest.getAddresses().size());

    }
}