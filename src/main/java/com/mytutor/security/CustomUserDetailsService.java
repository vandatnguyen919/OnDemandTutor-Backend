/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.security;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.Role;
import com.mytutor.entities.Account;
import com.mytutor.repositories.AccountRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Nguyen Van Dat
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email not found"));
        if (account.getStatus() == AccountStatus.ACTIVE
                || account.getStatus() == AccountStatus.PROCESSING) {
            return new User(account.getEmail(), account.getPassword(), mapRolesToAuthorities(account.getRole()));
        }
        throw new UsernameNotFoundException("This acount can not be trusted");
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(Role role) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()));
//        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName().toUpperCase())).collect(Collectors.toList());
    }

}
