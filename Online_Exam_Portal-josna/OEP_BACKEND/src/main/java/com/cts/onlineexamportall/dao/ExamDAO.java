package com.cts.onlineexamportall.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.onlineexamportall.model.Exam;
import com.cts.onlineexamportall.model.Question;

@Repository
public interface ExamDAO extends JpaRepository<Exam, Long> {

    Logger logger = LogManager.getLogger(ExamDAO.class);

    Exam findByTitle(String title);

    default Exam findByTitleWithLogging(String title) {
        logger.info("Finding exam by title: {}", title);
        Exam exam = findByTitle(title);
        if (exam != null) {
            logger.info("Exam found: {}", exam);
        } else {
            logger.warn("Exam not found with title: {}", title);
        }
        return exam;
    }

    List<Question> getAllQuestionsByExamId(long examId);
}