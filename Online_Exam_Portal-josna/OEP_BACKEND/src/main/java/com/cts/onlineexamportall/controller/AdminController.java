package com.cts.onlineexamportall.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cts.onlineexamportall.dto.Response;
import com.cts.onlineexamportall.exception.PasswordMisMatchException;
import com.cts.onlineexamportall.exception.ReportNotFoundException;
import com.cts.onlineexamportall.model.Report;
import com.cts.onlineexamportall.model.User;
import com.cts.onlineexamportall.service.ReportService;
import com.cts.onlineexamportall.service.UserService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//===========================================================
//================ ADMIN CONTROLLER =========================
//===========================================================

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS, RequestMethod.DELETE})
public class AdminController {                      

    private static final Logger logger = LogManager.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService reportService;

    @GetMapping("/getAllUsers")
    public ResponseEntity<Response<?>> getAllUsers() {
        try {
            logger.info("Fetching all users");
            List<User> user = userService.getAllUser();
            Response<List<User>> response = new Response<>(true, HttpStatus.OK, user, null);
            logger.info("Successfully fetched all users");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            logger.error("Error fetching users: {}", ex.getMessage());
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<Response<?>> deleteByUsername(@PathVariable String username) {
        try {
            logger.info("Deleting user with username: {}", username);
            userService.deleteByUsername(username);
            Response<String> response = new Response<>(true, HttpStatus.OK, "User deleted successfully", null);
            logger.info("Successfully deleted user with username: {}", username);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PasswordMisMatchException ex) {
            logger.error("Error deleting user: {}", ex.getMessage());
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getReports/{username}")
    public ResponseEntity<Response<?>> getReports(@PathVariable String username) {
        try {
            logger.info("Fetching reports for user with username: {}", username);
            List<Report> reports = reportService.getReportsByUserName(username);
            Response<List<Report>> response = new Response<>(true, HttpStatus.OK, reports, null);
            logger.info("Successfully fetched reports for user with username: {}", username);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            logger.error("Error fetching reports: {}", ex.getMessage());
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, "No reports found!", ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAllReports")
    public ResponseEntity<Response<?>> getAllReports(){
        try{
            logger.info("Fetching Reports in the database");
            List<Report> reports = reportService.getAllReports();
            Response<List<Report>> response = new Response<>(true, HttpStatus.OK, reports, null);
            logger.info("Successfully fetched all reports");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch(ReportNotFoundException ex){
            logger.error("Error fetching reports: {}", ex.getMessage());
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, "No reports found!", ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

}