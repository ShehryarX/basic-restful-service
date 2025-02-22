package ca.shehryar.mobileapprestfulws.ui.controller;

import ca.shehryar.mobileapprestfulws.exceptions.UserServiceException;
import ca.shehryar.mobileapprestfulws.service.AddressService;
import ca.shehryar.mobileapprestfulws.service.UserService;
import ca.shehryar.mobileapprestfulws.shared.dto.AddressDto;
import ca.shehryar.mobileapprestfulws.shared.dto.UserDto;
import ca.shehryar.mobileapprestfulws.ui.model.request.PasswordResetModel;
import ca.shehryar.mobileapprestfulws.ui.model.request.PasswordResetRequestModel;
import ca.shehryar.mobileapprestfulws.ui.model.request.UserDetailsRequestModel;
import ca.shehryar.mobileapprestfulws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
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
        produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json" }
    )
    public CollectionModel<AddressRest> getUserAddresses(@PathVariable String id) {
        List <AddressRest> returnVal = new ArrayList<>();

        List<AddressDto> addressesDto = addressService.getAddresses(id);

        if (addressesDto != null && !addressesDto.isEmpty()) {
            ModelMapper modelMapper = new ModelMapper();
            Type listType = new TypeToken<List<AddressRest>>(){}.getType();
            returnVal = modelMapper.map(addressesDto, listType);

            for (AddressRest addressRest : returnVal) {
                Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId()))
                        .withSelfRel();
                addressRest.add(addressLink);

                Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
                addressRest.add(userLink);
            }
        }

        return new CollectionModel<>(returnVal);
    }

    @GetMapping(
        path = "/{userId}/addresses/{addressId}",
        produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json" }
    )
    public EntityModel<AddressRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
        AddressDto addressDto = addressService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();
        AddressRest returnVal = modelMapper.map(addressDto, AddressRest.class);

        Link userLink = linkTo(methodOn(UserController.class).getUser(userId))
                .withRel("user ");

        Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId))
                .withRel("addresses");

        Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId))
                .withSelfRel();

        returnVal.add(userLink);
        returnVal.add(addressesLink);
        returnVal.add(addressLink);

        return new EntityModel<>(returnVal);
    }

    @GetMapping(
            path = "/email-verification",
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json" }
    )
    public OperationStatus verifyEmailToken(@RequestParam(value = "token") String token) {
        OperationStatus returnVal = new OperationStatus();
        returnVal.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        if (isVerified) {
            returnVal.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            returnVal.setOperationResult(RequestOperationStatus.FAILED.name());
        }

        return returnVal;
    }

    @PostMapping(
            path = "/password-reset-request",
            consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public OperationStatus requestReset(@RequestBody PasswordResetRequestModel body) {
        OperationStatus returnVal = new OperationStatus();

        boolean operation = userService.requestPasswordReset(body.getEmail());

        returnVal.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        returnVal.setOperationResult(RequestOperationStatus.FAILED.name());

        if (operation) {
            returnVal.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnVal;
    }

    @PostMapping(
            path = "/password-reset",
            consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public OperationStatus resetPassword(@RequestBody PasswordResetModel body) {
        OperationStatus returnVal = new OperationStatus();

        boolean operation = userService.resetPassword(body.getToken(), body.getPassword());

        returnVal.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        returnVal.setOperationResult(RequestOperationStatus.FAILED.name());

        if (operation) {
            returnVal.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnVal;
    }
}
