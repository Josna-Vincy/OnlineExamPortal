package com.cts.onlineexamportall.service;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cts.onlineexamportall.exception.ExamNotFoundException;
import com.cts.onlineexamportall.exception.InvalidResponseException;
import com.cts.onlineexamportall.dao.ExamDAO;
import com.cts.onlineexamportall.dao.LeaderboardDAO;
import com.cts.onlineexamportall.dao.QuestionDAO;
import com.cts.onlineexamportall.dao.ReportDAO;
import com.cts.onlineexamportall.dao.StudentExamResponseDAO;
import com.cts.onlineexamportall.dao.UserDAO;
import com.cts.onlineexamportall.dto.ExamAnswersDTO;
import com.cts.onlineexamportall.dto.ExamDTO;
import com.cts.onlineexamportall.dto.ExamResponseDTO;
import com.cts.onlineexamportall.dto.QuestionDTO;
import com.cts.onlineexamportall.exception.UserNotFoundException;
import com.cts.onlineexamportall.model.Exam;
import com.cts.onlineexamportall.model.Question;
import com.cts.onlineexamportall.model.Report;
import com.cts.onlineexamportall.model.StudentExamResponse;
import com.cts.onlineexamportall.model.User;

@Service
public class ExamService {

    private final LeaderboardService leaderboardService;

    private static final Logger logger = LogManager.getLogger(ExamService.class);

    @Autowired
    private ExamDAO examDAO;

    @Autowired
    private QuestionDAO questionDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ReportDAO reportDAO;

    @Autowired
    private StudentExamResponseDAO responseDAO;

    @Autowired
    private LeaderboardDAO leaderboardDAO;

    ExamService(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    public boolean examExists(long examId){
        logger.info("Checking if exam exists with ID: {}", examId);
        Optional<Exam> optionalExam = examDAO.findById(examId);
        boolean exists = optionalExam.isPresent();
        logger.info("Exam exists: {}", exists);
        return exists;
    }

    public boolean examNameExists(String examName){
        logger.info("Checking if exam exists with name: {}", examName);
        Exam exam = examDAO.findByTitle(examName);
        boolean exists = exam != null;
        logger.info("Exam name exists: {}", exists);
        return exists;
    }

    public List<Exam> getAllExams() {
        try {
            logger.info("Fetching all exams");
            List<Exam> exams = examDAO.findAll();
            if (exams.isEmpty()) {
                logger.warn("No exams found");
                throw new RuntimeException("No exams found");
            }
            logger.info("Exams found: {}", exams.size());
            return exams;
        } catch (Exception e) {
            logger.error("Error occurred while fetching exams: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch exams", e);
        }
    }

    public void deleteExam(long examId) {
        logger.info("Deleting exam with ID: {}", examId);
        @SuppressWarnings("deprecation")
        Exam exam = examDAO.getById(examId);

        if (exam == null) {
            logger.error("Exam not found with ID: {}", examId);
            throw new UserNotFoundException("Exam not found!");
        }

        examDAO.delete(exam);
        logger.info("Exam deleted with ID: {}", examId);
    }

    public Exam createExam(String category, ExamDTO examDTO) {
        logger.info("Creating exam with title: {}", examDTO.getTitle());
        Exam exam = new Exam();
        exam.setTitle(examDTO.getTitle());
        exam.setDuration(examDTO.getDuration());
        exam.setDescription(examDTO.getDescription());
        exam.setTotalMarks(examDTO.getTotalMarks());

        List<Question> questions = questionDAO.findAllByCategory(category);
        exam.setQuestions(questions);

        examDAO.save(exam);
        logger.info("Exam created with ID: {}", exam.getExamId());

        return exam;
    }

    public Exam updateExam(long id, ExamDTO examDTO){
        logger.info("Updating exam with ID: {}", id);
        Optional<Exam> optionalExam = examDAO.findById(id);
        if (optionalExam.isPresent()) {
            Exam exam = optionalExam.get();
            if (examDTO.getTitle() != null) {
                exam.setTitle(examDTO.getTitle());
            }
            if (examDTO.getDuration() != 0) {
                exam.setDuration(examDTO.getDuration());
            }
            if (examDTO.getDescription() != null) {
                exam.setDescription(examDTO.getDescription());
            }
            if (examDTO.getTotalMarks() != 0) {
                exam.setTotalMarks(examDTO.getTotalMarks());
            }

            examDAO.save(exam);
            logger.info("Exam updated with ID: {}", id);
            return exam;
        } else {
            logger.error("Exam not found with ID: {}", id);
            throw new RuntimeException("Exam not found");
        }
    }

    public void updateSelectedQuestions(long examId, List<QuestionDTO> updatedQuestions) {
        Optional<Exam> optionalExam = examDAO.findById(examId);
        if (optionalExam.isPresent()) {
            Exam exam = optionalExam.get();
            
            List<Question> existingQuestions = examDAO.getAllQuestionsByExamId(examId);
            
            for (QuestionDTO updatedQuestion : updatedQuestions) {
                for( int i = 0 ; i < existingQuestions.size() ; i++ ){
                    if( updatedQuestion.getDescription() == existingQuestions.get(i).getDescription()){
                        existingQuestions.get(i).setCategory(updatedQuestion.getCategory());
                        existingQuestions.get(i).setDifficulty(updatedQuestion.getDifficulty());
                        existingQuestions.get(i).setCategory(updatedQuestion.getCorrectAnswer());
                    }
                }
            }
            exam.setQuestions(existingQuestions);
            examDAO.save(exam);
        } else {
            throw new ExamNotFoundException("Exam not found");
        }
    }

    public void updateAQuestionInExam(long examId, String originalDescription, QuestionDTO updatedQuestionDTO) {
        Optional<Exam> optionalExam = examDAO.findById(examId);

        if( optionalExam == null ){
            throw new ExamNotFoundException("Exam doesnt exist");
        }
        Exam exam = optionalExam.get();

        List<Question> existingQuestions = examDAO.getAllQuestionsByExamId(examId);

            for( Question quest : existingQuestions ){
                if( quest.getDescription().equals(originalDescription) ){
                    quest.setDescription(updatedQuestionDTO.getDescription());
                    quest.setCategory(updatedQuestionDTO.getCategory());
                    quest.setCorrectAnswer(updatedQuestionDTO.getCorrectAnswer());
                    quest.setDifficulty(updatedQuestionDTO.getDifficulty());
                }
            }
            
        exam.setQuestions(existingQuestions);
        examDAO.save(exam);
    }

    //=============================================
    //=========== Response Module =================
    //=============================================

    public boolean responseExist(long examId, String username){ 
        logger.info("Checking if response exists for exam ID: {} and username: {}", examId, username);
        boolean exists = !responseDAO.findByExamIdAndUserName(examId, username).isEmpty();
        logger.info("Response exists: {}", exists);
        return exists;
    }

    public Report generateReport(long examId, String username) {
        logger.info("Generating report for exam ID: {} and username: {}", examId, username);
        Optional<Exam> optionalExam = examDAO.findById(examId);
        Exam exam = null;

        Optional<Report> existingReport = reportDAO.findByExamIdAndUserName(examId, username);
        if (existingReport.isPresent()) {
            logger.warn("Report already exists for exam ID: {} and username: {}", examId, username);
            // return existingReport.get(); //Option 1: return the existing report.
            throw new IllegalStateException("Report already exists for exam ID: " + examId + " and username: " + username); //Option 2: Throw an exception.
        }


        if (optionalExam.isEmpty()) { 
            logger.error("Exam not found with ID: {}", examId);
            throw new ExamNotFoundException("Exam with Id " + examId + " not found!");
        } else { 
            exam = optionalExam.get();
        }

        Report report = new Report();
        try {
            List<StudentExamResponse> responses = responseDAO.findByExamIdAndUserName(examId, username);
            
            if (responses.isEmpty()) {
                logger.warn("No responses found for exam ID: {} and username: {}", examId, username);
                throw new InvalidResponseException("No responses found for the given examId and username");
            }
    
            int totalQuestions = responses.size();
            int correctAnswers = 0;
    
            for (StudentExamResponse response : responses) {
                @SuppressWarnings("deprecation")
                Question question = questionDAO.getById(response.getQuestionId());
                if (question == null) {
                    logger.error("Question not found for question ID: {}", response.getQuestionId());
                    throw new IllegalArgumentException("Question not found for questionId: " + response.getQuestionId());
                }
    
                if (response.getAnswer().equals(question.getCorrectAnswer())) {
                    correctAnswers++;
                }
            }
    
            double percentage = (double) correctAnswers / totalQuestions * 100;
            double score = (double) correctAnswers / totalQuestions * exam.getTotalMarks();

            report.setUserName(username);
            report.setExamId(examId);
            report.setTotalQuestions(totalQuestions);
            report.setCorrectAnswers(correctAnswers);
            report.setScore(score);
            report.setPercentage(percentage);

            reportDAO.save(report);
            logger.info("Report generated for exam ID: {} and username: {}", examId, username);

            List<Report> reports = reportDAO.findByExamId(examId); // Assuming you have this method
            leaderboardService.createLeaderboardEntries(examId, reports);
    
        } catch (IllegalArgumentException e) {
            logger.error("Error: {}", e.getMessage());
        } catch (InvalidResponseException e) {
            logger.error("An unexpected error occurred: {}", e.getMessage());
            e.printStackTrace();
        }
        return report;
    }

    @SuppressWarnings("deprecation")
    public void submitResponse(ExamResponseDTO responseDTO) {
        logger.info("Submitting response for exam ID: {} and username: {}", responseDTO.getExamId(), responseDTO.getUserName());
        try {
            User user = userDAO.findByUserName(responseDTO.getUserName());
            if (user == null) {
                logger.error("User not found with username: {}", responseDTO.getUserName());
                throw new UserNotFoundException("User not found");
            }
    
            Exam exam = examDAO.getById(responseDTO.getExamId());
            if (exam == null) {
                logger.error("Exam not found with ID: {}", responseDTO.getExamId());
                throw new ExamNotFoundException("Exam not found");
            }

            for (ExamAnswersDTO answer : responseDTO.getAnswers()) {
                StudentExamResponse responseBy = new StudentExamResponse();
                responseBy.setUserName(user.getUserName());
                responseBy.setExamId(exam.getExamId());
                responseBy.setQuestionId(answer.getQuestionId());
                responseBy.setAnswer(answer.getAnswer());
    
                responseDAO.save(responseBy);
            }
            logger.info("Responses submitted for exam ID: {} and username: {}", responseDTO.getExamId(), responseDTO.getUserName());
        } catch (UserNotFoundException e) {
            logger.error("Error: {}", e.getMessage());
        } catch (ExamNotFoundException e) {
            logger.error("An unexpected error occurred: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public Exam getExamById(long examId) {
        return examDAO.getReferenceById(examId);
    }

    

   
 

}