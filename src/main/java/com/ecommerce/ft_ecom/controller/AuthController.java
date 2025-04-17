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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
            if (authentication.isAuthenticated()) {
                String token = jwtUtils.generateToken(userDetailsImpl.getUsername());
//                ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetailsImpl);
//                response.addHeader("Set-Cookie", jwtCookie.toString());
                // Create JWT cookie
                Cookie jwtCookie = new Cookie("jwt", token);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setSecure(true); // Use only with HTTPS
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge((int) (expirationTime / 1000)); // Convert ms to seconds
                jwtCookie.setAttribute("SameSite", "Strict");
                response.addCookie(jwtCookie);

                List<String> roles = userDetailsImpl.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

                LoginResponse loginResponse = new LoginResponse(userDetailsImpl.getId(), jwtCookie.toString(),
                        userDetailsImpl.getUsername(), roles);

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                                jwtCookie.toString())
                        .body(loginResponse);
                //return ResponseEntity.ok().body(new MessageResponse("Login successful"));
            } else {
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
            if (userRole == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new MessageResponse("Error: Default role USER not found"));
            }
            roles.add(userRole);
        } else {
            for (String role : stringRole){
                switch (role.toLowerCase()){
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
            }
        }
        user.setRole(roles);
        userRepository.save(user);
        return ResponseEntity.ok().body(new MessageResponse("Account Created Successfully"));
    }

    @GetMapping("/username")
    public String username(Authentication authentication){
        if (authentication.isAuthenticated()){
            return authentication.getName();
        } else {
            return "NULL";
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> userDetails(Authentication authentication){
        UserDetailsImpl userDetailsimpl = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetailsimpl.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        LoginResponse loginResponse = new LoginResponse(userDetailsimpl.getId(), userDetailsimpl.getUsername(), roles);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @GetMapping("/signout")
    public ResponseEntity<?> signOut(HttpServletRequest request, HttpServletResponse response) {
        // Clear the JWT cookie
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Delete the cookie
        jwtCookie.setAttribute("SameSite", "Strict");
        response.addCookie(jwtCookie);
//        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();

        // Clear the security context
        SecurityContextHolder.clearContext();

        // Invalidate the session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok().body(new MessageResponse("Logged out successfully"));
    }
}

//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
//        );
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        UserDetailsImpl userDetailsimpl = (UserDetailsImpl) authentication.getPrincipal();
//        if (authentication.isAuthenticated()){
////            ResponseCookie token = jwtUtils.generateJwtCookie(userDetailsimpl);
//            String token = jwtUtils.generateToken(userDetailsimpl.getUsername());
//            System.out.println("Generated Cookie: " + token);
//            List<String> roles = userDetailsimpl.getAuthorities().stream()
//                    .map(GrantedAuthority::getAuthority)
//                    .toList();
//            Date time = new Date(System.currentTimeMillis()+expirationTime);
//            LoginResponse resp = new LoginResponse(token, loginRequest.getUsername(), roles, time);
//            return ResponseEntity.ok().body(resp);
////            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
////                    token.toString()).body(new LoginResponse(token.toString(), loginRequest.getUsername(), roles, time));
//        }
//        else {
//            throw new RuntimeException("Wrong Username or Password");
//        }
//    }
