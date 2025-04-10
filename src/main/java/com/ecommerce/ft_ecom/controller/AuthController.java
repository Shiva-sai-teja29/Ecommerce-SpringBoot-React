package com.ecommerce.ft_ecom.controller;

import com.ecommerce.ft_ecom.model.Role;
import com.ecommerce.ft_ecom.model.Roles;
import com.ecommerce.ft_ecom.model.User;
import com.ecommerce.ft_ecom.repository.RoleRepository;
import com.ecommerce.ft_ecom.repository.UserRepository;
import com.ecommerce.ft_ecom.security.response.MessageResponse;
import com.ecommerce.ft_ecom.security.request.SignUpRequest;
import com.ecommerce.ft_ecom.security.jwt.JwtUtils;
import com.ecommerce.ft_ecom.security.request.LoginRequest;
import com.ecommerce.ft_ecom.security.response.LoginResponse;
import com.ecommerce.ft_ecom.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Value("${project.time}")
    private Long expirationTime;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (authentication.isAuthenticated()){
            String token = jwtUtils.generateToken(loginRequest.getUsername());
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            Date time = new Date(System.currentTimeMillis()+expirationTime);
            return new LoginResponse(token, loginRequest.getUsername(), roles, time);
        }
        else {
            throw new RuntimeException("Wrong Username or Password");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest){
        if (userRepository.existsByUsername(signUpRequest.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("Username already Exists"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Email already Exists"));
        }

        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), passwordEncoder.encode(signUpRequest.getPassword()));
        Set<String> stringRole = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        if (stringRole == null){
            Role userRole = roleRepository.findByRoleName(Roles.ROLE_USER);
            roles.add(userRole);
        } else {
            stringRole.forEach(role -> {
                switch (role){
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(Roles.ROLE_ADMIN);
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(Roles.ROLE_SELLER);
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(Roles.ROLE_USER);
                        roles.add(userRole);
                        break;
                }
            });
        }
        user.setRole(roles);
        userRepository.save(user);
        return ResponseEntity.ok().body(new MessageResponse("Account Created Successfully"));
    }
}
