package com.ecommerce.ft_ecom.repository;

import com.ecommerce.ft_ecom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String username);
}
