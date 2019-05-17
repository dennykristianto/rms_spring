package com.mitrais.rms.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Collection;

@Data
@Entity
public class User implements Serializable {
    public interface Create { }

    public interface BasicUpdate { }

    public interface PasswordUpdate { }

    public enum Role {ROLE_USER, ROLE_ADMIN}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Username is required", groups = {Create.class, BasicUpdate.class})
    @Column(unique = true)
    private String username;

    @NotEmpty(message = "Password is required", groups = {Create.class, PasswordUpdate.class})
    private String password;

    @NotEmpty(message = "Name is required", groups = {Create.class, BasicUpdate.class})
    private String name;

    @NotEmpty(message = "Address is required", groups = {Create.class, BasicUpdate.class})
    private String address;

    @NotEmpty(message = "Email is required", groups = {Create.class, BasicUpdate.class})
    @Email(message = "Email is not well correctly formed", groups = {Create.class, BasicUpdate.class})
    private String email;

    private String facebook;
    private String google;
    private String linkedin;
    private String twitter;

    private String picture;

    private Role role;


    public User(){};
    public User(int id){this.setId(id);}
}
