package com.ecommerce.ft_ecom.repository;

import com.ecommerce.ft_ecom.model.Role;
import com.ecommerce.ft_ecom.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(Roles roles);
}
