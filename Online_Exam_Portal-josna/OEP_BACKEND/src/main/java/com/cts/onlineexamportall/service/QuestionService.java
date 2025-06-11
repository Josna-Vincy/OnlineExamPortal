package com.cts.onlineexamportall.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cts.onlineexamportall.dao.QuestionDAO;
import com.cts.onlineexamportall.dto.QuestionDTO;
import com.cts.onlineexamportall.exception.QuestionNotFoundException;
import com.cts.onlineexamportall.model.Question;

import jakarta.transaction.Transactional;

@Service
public class QuestionService {

    private static final Logger logger = LogManager.getLogger(QuestionService.class);

    @Autowired
    private QuestionDAO questionDAO;

    public List<Question> getAllQuestions() {
        try {
            logger.info("Fetching all questions");
            List<Question> questions = questionDAO.findAll();
            if (questions.isEmpty()) {
                logger.warn("No questions found");
                throw new RuntimeException("No questions found");
            }
            logger.info("Questions found: {}", questions.size());
            return questions;
        } catch (Exception e) {
            logger.error("Error occurred while fetching questions: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch questions", e);
        }
    }

    @Transactional
    public void createMultipleQuestions(List<QuestionDTO> questions) {
        try {
            logger.info("Creating multiple questions");
            List<Question> questionEntities = questions.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
            questionDAO.saveAll(questionEntities);
            logger.info("Questions saved successfully");
        } catch (Exception e) {
            logger.error("Error occurred while saving questions: {}", e.getMessage());
            throw new RuntimeException("Failed to save questions", e);
        }
    }

    private Question convertToEntity(QuestionDTO questionDTO) {
        Question question = new Question();
        question.setDescription(questionDTO.getDescription());
        question.setCategory(questionDTO.getCategory());
        question.setDifficulty(questionDTO.getDifficulty());
        question.setCorrectAnswer(questionDTO.getCorrectAnswer());
        return question;
    }

    @Transactional
    public Question updateQuestion(Long id, QuestionDTO questionDTO) {
        logger.info("Updating question with ID: {}", id);
        Optional<Question> optionalQuestion = questionDAO.findById(id);
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            if (questionDTO.getDescription() != null) {
                question.setDescription(questionDTO.getDescription());
            }
            if (questionDTO.getCategory() != null) {
                question.setCategory(questionDTO.getCategory());
            }
            if (questionDTO.getDifficulty() != null) {
                question.setDifficulty(questionDTO.getDifficulty());
            }
            if (questionDTO.getCorrectAnswer() != null) {
                question.setCorrectAnswer(questionDTO.getCorrectAnswer());
            }
            logger.info("Question updated successfully with ID: {}", id);
            return questionDAO.save(question);
        } else {
            logger.error("Question not found with ID: {}", id);
            throw new QuestionNotFoundException("Question not found");
        }
    }

    public void createByCategory(String category, List<QuestionDTO> questions) {
        logger.info("Creating questions by category: {}", category);
        List<Question> questionsUpdated = new ArrayList<>(); 

        for (QuestionDTO question : questions) {
            question.setCategory(category);
            Question formalQuestion = convertToEntity(question);
            questionsUpdated.add(formalQuestion);
        }

        questionDAO.saveAll(questionsUpdated);
        logger.info("Questions created successfully for category: {}", category);
    }

    public void deleteQuestion(long id) {
        logger.info("Deleting question with ID: {}", id);
        Optional<Question> optionalQuestion = questionDAO.findById(id);

        if (optionalQuestion.isEmpty()) {
            logger.error("Question not found with ID: {}", id);
            throw new QuestionNotFoundException("Question not found");
        }

        questionDAO.delete(optionalQuestion.get());
        logger.info("Question deleted successfully with ID: {}", id);
    }

    public Question createQuestion(QuestionDTO question) {
        logger.info("Creating single question");
        Question questions = convertToEntity(question);
        questionDAO.save(questions);
        logger.info("Question created successfully");
        return questions;
    }

    public boolean categoryExists(String category) {
        logger.info("Checking if category exists: {}", category);
        List<Question> questions = questionDAO.findAllByCategory(category);
        boolean exists = !questions.isEmpty();
        logger.info("Category exists: {}", exists);
        return exists;
    }
}