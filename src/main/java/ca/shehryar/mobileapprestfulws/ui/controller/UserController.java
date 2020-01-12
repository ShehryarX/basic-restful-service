package ca.shehryar.mobileapprestfulws.ui.controller;

import ca.shehryar.mobileapprestfulws.service.UserService;
import ca.shehryar.mobileapprestfulws.shared.dto.UserDto;
import ca.shehryar.mobileapprestfulws.ui.model.request.UserDetailsRequestModel;
import ca.shehryar.mobileapprestfulws.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public String getUser() {
        return "Get User";
    }

    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnVal = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnVal);

        return returnVal;
    }

    @PutMapping
    public String updateUser() {
        return "Update user";
    }

    @DeleteMapping
    public String deleteUser() {
        return "Delete user";
    }

}
