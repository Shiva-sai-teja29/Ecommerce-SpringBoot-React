package com.ecommerce.ft_ecom.service;

import com.ecommerce.ft_ecom.dtos.AddressDTO;
import com.ecommerce.ft_ecom.exceptions.ResourceNotFoundException;
import com.ecommerce.ft_ecom.model.Address;
import com.ecommerce.ft_ecom.model.User;
import com.ecommerce.ft_ecom.repository.AddressRepository;
import com.ecommerce.ft_ecom.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public AddressDTO addAddressToUser(AddressDTO addressDTO, User user) {

        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(user);
        List<Address> addresses = user.getAddresses();
        addresses.add(address);
        user.setAddresses(addresses);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO getAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressesByUser(User user) {
        List<Address> addresses = user.getAddresses();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setPincode(addressDTO.getPincode());
        address.setCountry(addressDTO.getCountry());
        address.setBuildingName(addressDTO.getBuildingName());
        address.setStreet(addressDTO.getStreet());

        Address updateAddress = addressRepository.save(address);

        User user = address.getUser();
        user.getAddresses().removeIf(address1 -> address1.getAddressId().equals(addressId));
        user.getAddresses().add(updateAddress);
        userRepository.save(user);

        return modelMapper.map(updateAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        User user = address.getUser();
        user.getAddresses().removeIf(address1 -> address1.getAddressId().equals(addressId));
        userRepository.save(user);

        addressRepository.delete(address);
        return "Address deleted Successfully with addressId: "+addressId+".";
    }
}
