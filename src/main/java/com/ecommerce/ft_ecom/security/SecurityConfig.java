package com.ecommerce.ft_ecom.security;

import com.ecommerce.ft_ecom.model.Role;
import com.ecommerce.ft_ecom.model.Roles;
import com.ecommerce.ft_ecom.model.User;
import com.ecommerce.ft_ecom.repository.RoleRepository;
import com.ecommerce.ft_ecom.repository.UserRepository;
import com.ecommerce.ft_ecom.security.jwt.AuthEntryPoint;
import com.ecommerce.ft_ecom.security.jwt.JwtFilter;
import com.ecommerce.ft_ecom.security.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private AuthEntryPoint authEntryPoint;

    @Bean
    public JwtFilter jwtFilter(){
        return new JwtFilter();
    }

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request->
                        request.requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/api/auth/login", "/api/auth/signup", "/api/auth/signout").permitAll()
                                .requestMatchers("/error").permitAll()
                                .anyRequest().authenticated())
                .logout(logout->
                        logout.logoutUrl("/api/auth/logout")
                                .logoutSuccessHandler(((request, response, authentication) ->
                                        response.setStatus(HttpServletResponse.SC_OK)))
                                .logoutSuccessUrl("/api/auth/login?logout")
                                .invalidateHttpSession(true)
                                .deleteCookies("jwt")
                                .permitAll())
                .headers(header->
                        header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(session->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                          session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                        .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception->
                        exception.authenticationEntryPoint(authEntryPoint))
                .authenticationProvider(daoAuthenticationProvider());
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncode());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncode(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Create or retrieve roles
            Role userRole = roleRepository.findByRoleName(Roles.ROLE_USER);
            if (userRole == null) {
                userRole = roleRepository.save(new Role(Roles.ROLE_USER));
            }

            Role sellerRole = roleRepository.findByRoleName(Roles.ROLE_SELLER);
            if (sellerRole == null) {
                sellerRole = roleRepository.save(new Role(Roles.ROLE_SELLER));
            }

            Role adminRole = roleRepository.findByRoleName(Roles.ROLE_ADMIN);
            if (adminRole == null) {
                adminRole = roleRepository.save(new Role(Roles.ROLE_ADMIN));
            }

            // Refresh them from DB to make sure they're managed
            userRole = roleRepository.findByRoleName(Roles.ROLE_USER);
            sellerRole = roleRepository.findByRoleName(Roles.ROLE_SELLER);
            adminRole = roleRepository.findByRoleName(Roles.ROLE_ADMIN);

            // Define role sets
            Set<Role> userRoles = Set.of(userRole);
            Set<Role> sellerRoles = Set.of(sellerRole);
            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole); // Admin gets all roles

            // Create user1 if not exists
            if (!userRepository.existsByUsername("user1")) {
                User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"));
                user1.setRole(userRoles);
                userRepository.save(user1);
            }

            // Create seller1 if not exists
            if (!userRepository.existsByUsername("seller1")) {
                User seller1 = new User("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
                seller1.setRole(sellerRoles);
                userRepository.save(seller1);
            }

            // Create admin if not exists
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
                admin.setRole(adminRoles);
                userRepository.save(admin);
            }
        };
    }
}
