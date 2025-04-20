package com.ecommerce.ft_ecom.repository;

import com.ecommerce.ft_ecom.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
