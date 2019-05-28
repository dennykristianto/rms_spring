package com.mitrais.rms.service.impl;

import com.mitrais.rms.model.User;
import com.mitrais.rms.model.RmsUserDetails;
import com.mitrais.rms.repository.UserRepository;
import com.mitrais.rms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Override
    public User findUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findUserByUsername(String name) throws UsernameNotFoundException {
        return userRepository.findByUsername(name).orElseThrow(()-> new UsernameNotFoundException("Username cannot be found!"));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public RmsUserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user=findUserByUsername(s);
        return new RmsUserDetails.Builder()
                .id((long) user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .name(user.getName())
                .facebook(user.getFacebook())
                .google(user.getGoogle())
                .twitter(user.getTwitter())
                .addAuthorities(user.getRole().toString())
                .build();
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void saveUser(User user) {
        if(user.getPassword()!=null){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
