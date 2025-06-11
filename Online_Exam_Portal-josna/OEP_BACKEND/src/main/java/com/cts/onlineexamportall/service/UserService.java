
package com.cts.onlineexamportall.service;

import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.cts.onlineexamportall.dao.UserDAO;
import com.cts.onlineexamportall.enums.Role;
import com.cts.onlineexamportall.exception.UserNotFoundException;
import com.cts.onlineexamportall.model.User;

@Service
public class UserService  {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private AuthenticationManager authManager;
    
    @Autowired
    private JwtUtilService jwtService;
    
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public boolean userExists(String username) {
        logger.info("Checking if user exists with username: {}", username);
        User user = userDAO.findByUserName(username);
        boolean exists = user != null;
        logger.info("User exists: {}", exists);
        return exists;
    }
    
    public boolean userEmailExists(String username, String email) { 
        logger.info("Checking if user email exists for username: {} and email: {}", username, email);
        boolean exists = userExists(username) && userDAO.findByUserName(username).getEmail().equals(email);
        logger.info("User email exists: {}", exists);
        return exists;
    }

    public boolean passwordMatch(String userName, String password) {
        logger.info("Checking if password matches for username: {}", userName);
        User user = userDAO.findByUserName(userName);
        boolean matches = user != null && encoder.matches(password, user.getPassword());
        logger.info("Password matches: {}", matches);
        return matches;
    }

    public User register(String name, String email, String password, Role role) {
        logger.info("Registering new user with username: {}", name);
        User newUser = new User();
        newUser.setUserName(name);
        newUser.setEmail(email);
        newUser.setPassword(encoder.encode(password));
        newUser.setRoles(Collections.singleton(role));
        
        User savedUser = userDAO.save(newUser);
        logger.info("User registered successfully with username: {}", name);
        return savedUser;
    }
    
    public String verify(String username, String password) {
        logger.info("Verifying user with username: {}", username);
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        if (authentication.isAuthenticated()) {
            User authUser = userDAO.findByUserName(username);
            String token = jwtService.generateToken(authUser.getUserId(), username);
            logger.info("User verified successfully with username: {}", username);
            return token;
        } else {
            logger.error("Invalid login credentials for username: {}", username);
            throw new UserNotFoundException("Invalid Login Credentials...");
        }
    }

    public List<User> getAllUser() {
        logger.info("Fetching all users");
        List<User> users = userDAO.findAll();
        logger.info("Users fetched successfully");
        return users;
    }

    public User update(String username, String email, Role role) {
        logger.info("Updating user with username: {}", username);
        try { 
            User existingUser = userDAO.findByUserName(username);
            if (existingUser == null) { 
                logger.error("User not found with username: {}", username);
                throw new UserNotFoundException("User with provided username not found!");
            }

            existingUser.setEmail(email);
            existingUser.setRoles(new HashSet<>(Collections.singleton(role)));
            userDAO.save(existingUser);
            logger.info("User updated successfully with username: {}", username);
            return existingUser;
        } catch (UserNotFoundException ex) {
            logger.error("User not found with username: {}", username);
            throw new UserNotFoundException("User with provided username not found!");
        }
    }

    public User findByUserName(String userName) {
        logger.info("Fetching user with username: {}", userName);
        User user = userDAO.findByUserName(userName);
        logger.info("User fetched successfully with username: {}", userName);
        return user;
    }

    public User updatePassword(String userName, String oldPassword, String newPassword) {
        logger.info("Updating password for username: {}", userName);
        User user = findByUserName(userName);
        if (user == null) {
            logger.error("User not found with username: {}", userName);
            throw new UserNotFoundException("No User Found With Username: " + userName);
        }

        if (!encoder.matches(oldPassword, user.getPassword())) {
            logger.error("Old password is incorrect for username: {}", userName);
            throw new IllegalArgumentException("Old password is incorrect");
        }
        
        if (newPassword == null || newPassword.isEmpty()) {
            logger.error("New password cannot be null or empty for username: {}", userName);
            throw new IllegalArgumentException("New password cannot be null or empty");
        }

        user.setPassword(encoder.encode(newPassword));
        User updatedUser = userDAO.save(user);
        logger.info("Password updated successfully for username: {}", userName);
        return updatedUser;
    }

    public User getProfile(String username) {
        logger.info("Fetching profile for username: {}", username);
        try {
            User optionalUser = userDAO.findByUserName(username);
            logger.info("Profile fetched successfully for username: {}", username);
            return optionalUser;
        } catch (UserNotFoundException ex) {
            logger.error("User not found with username: {}", username);
            throw new UserNotFoundException("User with provided username not found!");
        }
    }

    public void deleteByUsername(String username) {
        logger.info("Deleting user with username: {}", username);
        User user = userDAO.findByUserName(username);
        if (user == null) {
            logger.error("User not found with username: {}", username);
            throw new UserNotFoundException("User with username not found");
        }

        userDAO.delete(user);
        logger.info("User deleted successfully with username: {}", username);
    }

    public String getRole(String username) {
        // TODO Auto-generated method stub
        return userDAO.findByUserName(username).getRoles().toString();
    }
}