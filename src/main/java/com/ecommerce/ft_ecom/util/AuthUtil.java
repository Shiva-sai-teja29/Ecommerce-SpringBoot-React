package com.ecommerce.ft_ecom.util;

import com.ecommerce.ft_ecom.model.User;
import com.ecommerce.ft_ecom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    @Autowired
    UserRepository userRepository;


    public String loggedInEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null){
            throw new UsernameNotFoundException("User Not found with username "+authentication.getName());
        } else {
            return user.getEmail();
        }
    }

    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null){
            throw new UsernameNotFoundException("User Not found with username "+authentication.getName());
        } else {
            return user;
        }
    }
}
