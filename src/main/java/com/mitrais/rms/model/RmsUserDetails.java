package com.mitrais.rms.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class RmsUserDetails implements UserDetails {

    public enum SocialMediaService {FACEBOOK, TWITTER, GOOGLE, LINKED_IN}

    public enum Role {ROLE_USER}

    private Role role;

    private SocialMediaService socialSignInProvider;

    private Long id;

    private String name;

    private String username;

    private String password;

    private String twitter;

    private String facebook;

    private String google;

    private Collection<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static class Builder{
        private RmsUserDetails rmsUserDetails;

        public Builder(){
            rmsUserDetails =new RmsUserDetails();
            rmsUserDetails.authorities=new ArrayList<>();
        }
        public Builder name(String name){
            rmsUserDetails.setName(name);
            return this;
        }
        public Builder username(String username){
            rmsUserDetails.setUsername(username);
            return this;
        }
        public Builder password(String password){
            rmsUserDetails.setPassword(password);
            return this;
        }
        public Builder id(Long id){
            rmsUserDetails.setId(id);
            return this;
        }
        public Builder facebook(String id){
            rmsUserDetails.setFacebook(id);
            return this;
        }
        public Builder twitter(String id){
            rmsUserDetails.setTwitter(id);
            return this;
        }
        public Builder google(String id){
            rmsUserDetails.setGoogle(id);
            return this;
        }
        public Builder addAuthorities(String authority){
            rmsUserDetails.authorities.add((GrantedAuthority) ()->authority);
            return this;
        }
        public RmsUserDetails build(){
            return rmsUserDetails;
        }
    }
}
