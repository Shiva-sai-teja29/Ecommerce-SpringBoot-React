package com.ecommerce.ft_ecom.security;

import com.ecommerce.ft_ecom.model.Role;
import com.ecommerce.ft_ecom.model.Roles;
import com.ecommerce.ft_ecom.model.User;
import com.ecommerce.ft_ecom.repository.RoleRepository;
import com.ecommerce.ft_ecom.repository.UserRepository;
import com.ecommerce.ft_ecom.security.jwt.AuthEntryPoint;
import com.ecommerce.ft_ecom.security.jwt.JwtFilter;
import com.ecommerce.ft_ecom.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
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
                .httpBasic(Customizer.withDefaults())
                .headers(header->
                        header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request->
                        request.requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/api/auth/**").permitAll()
                                .anyRequest().authenticated())
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception->
                        exception.authenticationEntryPoint(authEntryPoint));
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
    public CommandLineRunner initData(RoleRepository roleRepository){
        return args -> {
        Role userRole1 = roleRepository.findByRoleName(Roles.ROLE_USER);
        if (userRole1 == null) roleRepository.save(new Role(Roles.ROLE_USER));

        Role sellerRole1 = roleRepository.findByRoleName(Roles.ROLE_SELLER);
        if (sellerRole1 == null) roleRepository.save(new Role(Roles.ROLE_SELLER));

        Role adminRole1 = roleRepository.findByRoleName(Roles.ROLE_ADMIN);
        if (adminRole1 == null) roleRepository.save(new Role(Roles.ROLE_ADMIN));
        };
    }
}
