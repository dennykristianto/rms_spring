package com.mitrais.rms.config;

import com.mitrais.rms.helper.SecurityContext;
import com.mitrais.rms.helper.SimpleConnectionSignUp;
import com.mitrais.rms.helper.SimpleSignInAdapter;
import com.mitrais.rms.helper.SocialMediaUserRepository;
import com.mitrais.rms.model.User;
import com.mitrais.rms.model.UsrDetails;
import com.mitrais.rms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

@Configuration
@EnableSocial
public class SocialConfig implements SocialConfigurer {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserService userService;

    @Autowired
    private ConnectionFactoryLocator factoryLocator;

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        connectionFactoryConfigurer.addConnectionFactory(new FacebookConnectionFactory(environment.getProperty("facebook.client-id"),environment.getProperty("facebook.client-secret")));
        connectionFactoryConfigurer.addConnectionFactory(new GoogleConnectionFactory(environment.getProperty("google.appId"),environment.getProperty("google.appSecret")));
        connectionFactoryConfigurer.addConnectionFactory(new TwitterConnectionFactory(environment.getProperty("twitter.appId"),environment.getProperty("twitter.appSecret")));
//        connectionFactoryConfigurer.addConnectionFactory();
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new UserIdSource() {
            @Override
            public String getUserId() {
                return ((UsrDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getFacebook();
            }
        };
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        SocialMediaUserRepository repository = new SocialMediaUserRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
        repository.setConnectionSignUp(new SimpleConnectionSignUp());
        return repository;
    }

    @Bean
    public ProviderSignInController providerSignInController(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository) {
        ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, new SimpleSignInAdapter(userService));
        controller.setSignUpUrl("/account/signup");
        controller.setSignInUrl("/account/login");
        controller.setPostSignInUrl("/users");
        return controller;
    }
}
