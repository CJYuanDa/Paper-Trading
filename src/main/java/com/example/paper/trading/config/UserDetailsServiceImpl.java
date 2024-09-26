package com.example.paper.trading.config;

import com.example.paper.trading.model.Users;
import com.example.paper.trading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(userEmail).orElseThrow(() ->
                new UsernameNotFoundException("User details not found for the user" + userEmail));

        List<GrantedAuthority> roles = user
                .getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

        return new User(user.getEmail(), user.getPassword(), roles);
    }
}
