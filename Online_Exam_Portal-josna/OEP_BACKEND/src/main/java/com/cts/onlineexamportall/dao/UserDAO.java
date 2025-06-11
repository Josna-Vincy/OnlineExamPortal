package com.cts.onlineexamportall.dao;

import java.util.Optional;
import java.util.UUID;

import com.cts.onlineexamportall.model.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO extends JpaRepository<User, UUID> {

    Logger logger = LogManager.getLogger(UserDAO.class);

    User findByUserName(String username);

    default User findByUserNameWithLogging(String username) {
        logger.info("Finding user by username: {}", username);
        User user = findByUserName(username);
        if (user != null) {
            logger.info("User found: {}", user);
        } else {
            logger.warn("User not found with username: {}", username);
        }
        return user;
    }

    Optional<User> findByEmail(String email);

    default Optional<User> findByEmailWithLogging(String email) {
        logger.info("Finding user by email: {}", email);
        Optional<User> user = findByEmail(email);
        if (user.isPresent()) {
            logger.info("User found: {}", user.get());
        } else {
            logger.warn("User not found with email: {}", email);
        }
        return user;
    }
}