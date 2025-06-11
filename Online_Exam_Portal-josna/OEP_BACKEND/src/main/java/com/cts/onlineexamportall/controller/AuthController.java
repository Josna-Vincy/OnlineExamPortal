package com.cts.onlineexamportall.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.onlineexamportall.dto.LoginRequestDTO;
import com.cts.onlineexamportall.dto.Response;
import com.cts.onlineexamportall.dto.UserRequest;
import com.cts.onlineexamportall.exception.PasswordMisMatchException;
import com.cts.onlineexamportall.exception.UserExistsException;
import com.cts.onlineexamportall.exception.UserNotFoundException;
import com.cts.onlineexamportall.model.User;
import com.cts.onlineexamportall.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200") 
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<User> getAllUser() {
        logger.info("Fetching all users");
        return userService.getAllUser();
    }

    @PostMapping("/login")
    public ResponseEntity<Response<?>> loginUser(@Valid @RequestBody LoginRequestDTO request) {
        try {
            logger.info("Attempting to log in user: {}", request.getUsername());
            if (!userService.userExists(request.getUsername())) {
                throw new UserNotFoundException("User does not exist!");
            }

            if (!userService.passwordMatch(request.getUsername(), request.getPassword())) {
                throw new PasswordMisMatchException("Entered password is incorrect");
            }

            String token = userService.verify(request.getUsername(), request.getPassword());
            Response<String> response = new Response<>(true, HttpStatus.OK, token, null);
            logger.info("User logged in successfully: {}", request.getUsername());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error("Login failed for user: {}. Reason: {}", request.getUsername(), e.getMessage());
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (PasswordMisMatchException ex) {
            logger.error("Password mismatch for user: {}. Reason: {}", request.getUsername(), ex.getMessage());
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Response<?>> registerUser(@Valid @RequestBody UserRequest request) {
        try {
            logger.info("Registering user: {}", request.getUserName());
            if (userService.userExists(request.getUserName())) {
                throw new UserExistsException("User already exists!");
            }

            User user = userService.register(request.getUserName(), request.getEmail(), request.getPassword(), request.getRole());
            Response<User> response = new Response<>(true, HttpStatus.OK, user, null);
            logger.info("User registered successfully: {}", request.getUserName());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserExistsException e) {
            logger.error("Registration failed for user: {}. Reason: {}", request.getUserName(), e.getMessage());
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Response<?>> update(@Valid @RequestBody UserRequest request) {
        try {
            logger.info("Updating user: {}", request.getUserName());
            if (!userService.userExists(request.getUserName())) {
                throw new UserNotFoundException("User does not exist!");
            }

            if (!userService.passwordMatch(request.getUserName(), request.getPassword())) {
                throw new PasswordMisMatchException("Entered password is incorrect");
            }

            if (!userService.userEmailExists(request.getUserName(), request.getEmail())) {
                throw new BadCredentialsException("Email is incorrect");
            }

            User user = userService.update(request.getUserName(), request.getEmail(), request.getRole());
            Response<User> response = new Response<>(true, HttpStatus.OK, user, null);
            logger.info("User updated successfully: {}", request.getUserName());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error("Update failed for user: {}. Reason: {}", request.getUserName(), e.getMessage());
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<Response<?>> updatePassword(@Valid @RequestBody UserRequest request, @RequestParam String newPassword) {
        try {
            logger.info("Updating password for user: {}", request.getUserName());
            if (!userService.userExists(request.getUserName())) {
                throw new UserNotFoundException("User does not exist!");
            }

            if (!userService.userEmailExists(request.getUserName(), request.getEmail())) {
                throw new BadCredentialsException("Email is incorrect");
            }

            User user = userService.updatePassword(request.getUserName(), request.getPassword(), newPassword);
            Response<User> response = new Response<>(true, HttpStatus.OK, user, null);
            logger.info("Password updated successfully for user: {}", request.getUserName());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException ex) {
            logger.error("Password update failed for user: {}. Reason: {}", request.getUserName(), ex.getMessage());
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getRole")
    public ResponseEntity<Response<?>> getRole(@RequestParam String username) {
            logger.info("Fetching role for user: {}", username);
            if (!userService.userExists(username)) {
                throw new UserNotFoundException("User does not exist!");
            }

            String role = userService.getRole(username);
            Response<String> response = new Response<>(true, HttpStatus.OK, role, null);
            logger.info("Fetched role successfully for user: {}", username);
            return new ResponseEntity<>(response, HttpStatus.OK);
    }
}