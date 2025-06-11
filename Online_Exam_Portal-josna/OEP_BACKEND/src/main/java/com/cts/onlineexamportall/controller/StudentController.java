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

import com.cts.onlineexamportall.dto.ExamResponseDTO;
import com.cts.onlineexamportall.dto.Response;
import com.cts.onlineexamportall.exception.DuplicateAttemptException;
import com.cts.onlineexamportall.exception.ExamNotFoundException;
import com.cts.onlineexamportall.exception.PasswordMisMatchException;
import com.cts.onlineexamportall.exception.UserNotFoundException;
import com.cts.onlineexamportall.model.Exam;
import com.cts.onlineexamportall.model.Leaderboard;
import com.cts.onlineexamportall.model.Report;
import com.cts.onlineexamportall.model.User;
import com.cts.onlineexamportall.service.ExamService;
import com.cts.onlineexamportall.service.LeaderboardService;
import com.cts.onlineexamportall.service.ReportService;
import com.cts.onlineexamportall.service.UserService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/student")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS, RequestMethod.DELETE})
public class StudentController {

    private static final Logger logger = LogManager.getLogger(StudentController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ExamService examService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping("/profile")
    public ResponseEntity<Response<?>> getProfile(@RequestParam String username) {
        try {
            logger.info("Fetching profile for username: {}", username);
            User user = userService.getProfile(username);
            Response<User> response = new Response<>(true, HttpStatus.OK, user, null);
            logger.info("Profile fetched successfully for username: {}", username);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PasswordMisMatchException ex) {
            logger.error("Error fetching profile for username: {}. Reason: {}", username, ex.getMessage());
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/submitResponse")
    public ResponseEntity<Response<?>> submitResponse(@RequestBody ExamResponseDTO responseDTO) {
        try {
            logger.info("Submitting response for examId: {} and username: {}", responseDTO.getExamId(), responseDTO.getUserName());
            if (!userService.userExists(responseDTO.getUserName())) {
                throw new UserNotFoundException("User does not exist");
            }

            if (!examService.examExists(responseDTO.getExamId())) {
                throw new ExamNotFoundException("Exam with Id " + responseDTO.getExamId() + " does not exist");
            }

            if (examService.responseExist(responseDTO.getExamId(), responseDTO.getUserName())) {
                throw new DuplicateAttemptException("You are not allowed to re-attempt this exam");
            }

            examService.submitResponse(responseDTO);
            Response<String> response = new Response<>(true, HttpStatus.CREATED, "The response has been submitted!", null);
            logger.info("Response submitted successfully for examId: {} and username: {}", responseDTO.getExamId(), responseDTO.getUserName());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error("Error submitting response for examId: {} and username: {}. Reason: {}", responseDTO.getExamId(), responseDTO.getUserName(), e.getMessage());
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, "Error submitting the response", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (DuplicateAttemptException e) {
            logger.error("Duplicate attempt for examId: {} and username: {}. Reason: {}", responseDTO.getExamId(), responseDTO.getUserName(), e.getMessage());
            Response<String> response = new Response<>(false, HttpStatus.CONFLICT, "Error submitting the response", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/getExams")
    public ResponseEntity<Response<?>> getExams() {
        try {
            List<Exam> exams = examService.getAllExams();
            Response<List<Exam>> response = new Response<>(true, HttpStatus.OK, exams, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/report/{username}")
    public ResponseEntity<Response<?>> getMethodName(@PathVariable String username) {
        try {
            logger.info("Fetching reports for username: {}", username);
            List<Report> reports = reportService.getReportsByUserName(username);
            Response<List<Report>> response = new Response<>(true, HttpStatus.FOUND, reports, "Reports fetched successfully");
            logger.info("Reports fetched successfully for username: {}", username);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error("Error fetching reports for username: {}. Reason: {}", username, e.getMessage());
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    //leaderboard
    @GetMapping("/leaderboard/{examId}")
    public ResponseEntity<Response<?>> getLeaderboard(@PathVariable Long examId) {
        try {
            logger.info("Fetching leaderboard for examId: {}", examId);
            List<Leaderboard> leaderboard = leaderboardService.getLeaderboardByExamId(examId);
            Response<List<Leaderboard>> response = new Response<>(true, HttpStatus.OK, leaderboard, "Leaderboard fetched successfully");
            logger.info("Leaderboard fetched successfully for examId: {}", examId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching leaderboard for examId: {}. Reason: {}", examId, e.getMessage());
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}