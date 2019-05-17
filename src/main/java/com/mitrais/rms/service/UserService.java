package com.mitrais.rms.service;

import com.mitrais.rms.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User findUserById(int id);
    User findUserByUsername(String name);
    User findUserByEmail(String email);
    List<User> findAll();
    void saveUser(User user);
    void deleteUser(User user);
}
