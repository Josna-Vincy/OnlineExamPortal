package com.cts.onlineexamportall.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.onlineexamportall.model.StudentExamResponse;

@Repository
public interface StudentExamResponseDAO extends JpaRepository<StudentExamResponse, Long> {

    Logger logger = LogManager.getLogger(StudentExamResponseDAO.class);

    List<StudentExamResponse> findByExamIdAndUserName(long examId, String username);

    default List<StudentExamResponse> findByExamIdAndUserNameWithLogging(long examId, String username) {
        logger.info("Finding student exam responses by examId: {} and username: {}", examId, username);
        List<StudentExamResponse> responses = findByExamIdAndUserName(examId, username);
        if (responses.isEmpty()) {
            logger.warn("No student exam responses found for examId: {} and username: {}", examId, username);
        } else {
            logger.info("Student exam responses found: {}", responses.size());
        }
        return responses;
    }

    void deleteByExamIdAndUserName(long examId, String userName);

    default void deleteByExamIdAndUserNameWithLogging(long examId, String userName) {
        logger.info("Deleting student exam responses by examId: {} and username: {}", examId, userName);
        deleteByExamIdAndUserName(examId, userName);
        logger.info("Student exam responses deleted for examId: {} and username: {}", examId, userName);
    }
}