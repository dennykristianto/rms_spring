package com.mitrais.rms.repository;

import com.mitrais.rms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    public Optional<User> findByUsername(@NotNull(message = "Username is required") String username);
    public User findUserByEmail(@NotNull(message = "Email is required") @Email(message = "Email is not well correctly formed") String email);
    public List<User> findUserByLinkedin(String sid);
    public List<User> findUserByFacebook(String sid);
    public List<User> findUserByGoogle(String sid);
    public List<User> findUserByTwitter(String sid);

}
