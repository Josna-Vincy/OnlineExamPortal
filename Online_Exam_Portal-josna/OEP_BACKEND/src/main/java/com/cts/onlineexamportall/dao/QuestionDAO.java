package com.cts.onlineexamportall.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.onlineexamportall.model.Question;

@Repository
public interface QuestionDAO extends JpaRepository<Question, Long> {

    Logger logger = LogManager.getLogger(QuestionDAO.class);

    List<Question> findAllByCategory(String category);

    default List<Question> findAllByCategoryWithLogging(String category) {
        logger.info("Finding questions by category: {}", category);
        List<Question> questions = findAllByCategory(category);
        if (questions.isEmpty()) {
            logger.warn("No questions found for category: {}", category);
        } else {
            logger.info("Questions found: {}", questions.size());
        }
        return questions;
    }

    Question findByDescription(String description);
}