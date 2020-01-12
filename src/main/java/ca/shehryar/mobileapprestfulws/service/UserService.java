package ca.shehryar.mobileapprestfulws.service;

import ca.shehryar.mobileapprestfulws.shared.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto user);
}
