package com.mitrais.rms.helper;

import com.mitrais.rms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SocialMediaUserRepository extends JdbcUsersConnectionRepository {
    @Autowired
    private UserRepository userRepository;

    public SocialMediaUserRepository(DataSource dataSource, ConnectionFactoryLocator connectionFactoryLocator, TextEncryptor textEncryptor) {
        super(dataSource, connectionFactoryLocator, textEncryptor);
    }

    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        List<String> localIds = new ArrayList<>();
        switch (connection.getKey().getProviderId()){
            case "facebook":
                userRepository.findUserByFacebook(connection.getKey().getProviderUserId()).forEach(user -> localIds.add(String.valueOf(user.getId())));
                break;
            case "twitter":
                userRepository.findUserByTwitter(connection.getKey().getProviderUserId()).forEach(user -> localIds.add(String.valueOf(user.getId())));
                break;
            case  "google":
                userRepository.findUserByGoogle(connection.getKey().getProviderUserId()).forEach(user -> localIds.add(String.valueOf(user.getId())));
                break;
        }
        return localIds;
    }

    @Override
    public Set<String> findUserIdsConnectedTo(String s, Set<String> set) {
        return null;
    }
}
