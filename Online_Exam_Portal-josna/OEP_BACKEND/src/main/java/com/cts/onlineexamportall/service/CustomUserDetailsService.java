package com.cts.onlineexamportall.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cts.onlineexamportall.dao.UserDAO;
import com.cts.onlineexamportall.model.User;
import com.cts.onlineexamportall.model.UserPrincipal;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user by username: {}", username);
        User user = userDAO.findByUserName(username);

        if (user == null) {
            logger.error("No user found with the username: {}", username);
            throw new UsernameNotFoundException("No user found with the username: " + username);
        }

        logger.info("User found: {}", user);
        return new UserPrincipal(user);
    }
}