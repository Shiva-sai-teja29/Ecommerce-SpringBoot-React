package com.ecommerce.ft_ecom.controller;

import com.ecommerce.ft_ecom.dtos.AddressDTO;
import com.ecommerce.ft_ecom.model.User;
import com.ecommerce.ft_ecom.service.AddressService;
import com.ecommerce.ft_ecom.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    AuthUtil authUtil;

    @PostMapping("/address")
    public ResponseEntity<AddressDTO> addAddress(@RequestBody AddressDTO addressDTO){
        User user = authUtil.loggedInUser();
        AddressDTO address = addressService.addAddressToUser(addressDTO, user);
        return new ResponseEntity<>(address, HttpStatus.CREATED);
    }

    @GetMapping("/address")
    public ResponseEntity<List<AddressDTO>> getAllAddresses(){
        List<AddressDTO> addresses = addressService.getAllAddresses();
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @GetMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable Long addressId){
        AddressDTO address = addressService.getAddress(addressId);
        return new ResponseEntity<>(address, HttpStatus.CREATED);
    }

    @GetMapping("/users/address")
    public ResponseEntity<List<AddressDTO>> getAddressesByUser(){
        User user = authUtil.loggedInUser();
        List<AddressDTO> addresses = addressService.getAddressesByUser(user);
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @PutMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId,
                                                    @RequestBody AddressDTO addressDTO){
        AddressDTO address = addressService.updateAddress(addressId, addressDTO);
        return new ResponseEntity<>(address, HttpStatus.CREATED);
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId){
        String status = addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }
}
