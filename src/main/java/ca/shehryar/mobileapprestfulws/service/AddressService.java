package ca.shehryar.mobileapprestfulws.service;

import ca.shehryar.mobileapprestfulws.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> getAddresses(String userId);
    AddressDto getAddress(String addressId);
}
