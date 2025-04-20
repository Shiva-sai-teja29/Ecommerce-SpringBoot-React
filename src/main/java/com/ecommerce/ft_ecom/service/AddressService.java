package com.ecommerce.ft_ecom.service;

import com.ecommerce.ft_ecom.dtos.AddressDTO;
import com.ecommerce.ft_ecom.model.User;

import java.util.List;

public interface AddressService {
    AddressDTO addAddressToUser(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddress(Long addressId);

    List<AddressDTO> getAddressesByUser(User user);

    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);

    String deleteAddress(Long addressId);
}
