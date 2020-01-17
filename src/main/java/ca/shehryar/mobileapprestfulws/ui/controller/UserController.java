package ca.shehryar.mobileapprestfulws.ui.controller;

import ca.shehryar.mobileapprestfulws.exceptions.UserServiceException;
import ca.shehryar.mobileapprestfulws.service.AddressService;
import ca.shehryar.mobileapprestfulws.service.UserService;
import ca.shehryar.mobileapprestfulws.shared.dto.AddressDto;
import ca.shehryar.mobileapprestfulws.shared.dto.UserDto;
import ca.shehryar.mobileapprestfulws.ui.model.request.UserDetailsRequestModel;
import ca.shehryar.mobileapprestfulws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @GetMapping(
        produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public List<UserRest> getUsers(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "limit", defaultValue = "25") int limit
    ) {
        List<UserRest> returnVal = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);

        for (UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnVal.add(userModel);
        }

        return returnVal;
    }

    @GetMapping(
        path = "/{id}",
        produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public UserRest getUser(@PathVariable String id) {
        UserRest returnVal = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnVal);

        return returnVal;
    }

    @PostMapping(
        consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
        produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnVal = new UserRest();

        if (userDetails.getFirstName().isEmpty()) {
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        returnVal = modelMapper.map(createdUser, UserRest.class);

        return returnVal;
    }

    @PutMapping(
        path = "/{id}",
        consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
        produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnVal = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updatedUser, returnVal);

        return returnVal;
    }

    @DeleteMapping(
        path = "/{id}",
        consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
        produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public OperationStatus deleteUser(@PathVariable String id) {
        OperationStatus returnVal = new OperationStatus();

        userService.deleteUser(id);

        returnVal.setOperationName(RequestOperationName.DELETE.name());
        returnVal.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnVal;
    }

    @GetMapping(
            path = "/{id}/addresses",
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public List<AddressRest> getUserAddresses(@PathVariable String id) {
        List <AddressRest> returnVal = new ArrayList<>();

        List<AddressDto> addressesDto = addressService.getAddresses(id);

        if (addressesDto != null && !addressesDto.isEmpty()) {
            ModelMapper modelMapper = new ModelMapper();
            Type listType = new TypeToken<List<AddressRest>>(){}.getType();
            returnVal = modelMapper.map(addressesDto, listType);
        }

        return returnVal;
    }
}
