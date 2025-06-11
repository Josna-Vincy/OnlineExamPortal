package com.cts.onlineexamportall.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.onlineexamportall.dto.ExamDTO;
import com.cts.onlineexamportall.dto.QuestionDTO;
import com.cts.onlineexamportall.dto.Response;
import com.cts.onlineexamportall.exception.ExamExistsException;
import com.cts.onlineexamportall.exception.ExamNotFoundException;
import com.cts.onlineexamportall.exception.InvalidResponseException;
import com.cts.onlineexamportall.exception.MandatoryFieldMissingException;
import com.cts.onlineexamportall.exception.ReportNotFoundException;
import com.cts.onlineexamportall.model.Exam;
import com.cts.onlineexamportall.model.Question;
import com.cts.onlineexamportall.model.Report;
import com.cts.onlineexamportall.service.ExamService;
import com.cts.onlineexamportall.service.QuestionService;
import com.cts.onlineexamportall.service.ReportService;

@RestController
@RequestMapping("/examiner")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS, RequestMethod.PUT, RequestMethod.DELETE})
public class ExaminerController {

	// private static final Logger logger = LogManager.getLogger(StudentController.class);
    @Autowired
    private QuestionService questionService;
 
    @Autowired
    private ExamService examService;

    @Autowired   
    private ReportService reportService;
 
 
    //=============================================
    //============= Question Module ===============
    //=============================================
 
    @GetMapping("/allQuestions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        try {
            List<Question> questions = questionService.getAllQuestions();
            return ResponseEntity.ok(questions);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
 
    @PostMapping("/createQuestion")
    public ResponseEntity<Response<?>> createQuestion(@RequestBody QuestionDTO question){
        try {
			Question questions = questionService.createQuestion(question);
            Response<Question> response = new Response<Question>(true, HttpStatus.CREATED , questions, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
		}catch( IllegalArgumentException ex ) {
			Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, ex.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
    }
    @PostMapping("/createMultipleQuestions")
    public ResponseEntity<Response<?>> createMoreQuestions(@RequestBody List<QuestionDTO> questions){
        try {
			questionService.createMultipleQuestions(questions);
            Response<String> response = new Response<String>(true, HttpStatus.CREATED , "List of questions added successfully", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
		}catch( IllegalArgumentException ex ) {
			Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, ex.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
    }
 
    @PostMapping("/createByCategory")
    public ResponseEntity<Response<?>> createByCategory(@RequestParam String category  ,@RequestBody List<QuestionDTO> questions){
        try {
			questionService.createByCategory(category,questions);
            Response<String> response = new Response<String>(true, HttpStatus.CREATED , "List of questions with category "+ category +"added successfully", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
		}catch( IllegalArgumentException ex ) {
			Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, ex.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
    }
 
    @PutMapping("/updateQuestion/{id}")
    public ResponseEntity<Response<?>> updateQuestion(@PathVariable Long id, @RequestBody QuestionDTO questionDTO) {
       try {
			Question question = questionService.updateQuestion(id, questionDTO);
			Response<Question> response = new Response<>(true, HttpStatus.OK, question, null);
	        return new ResponseEntity<>(response, HttpStatus.OK);
		}catch( IllegalArgumentException ex ) {
			Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, ex.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
    }
 
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response<?>> deleteQuestion(@PathVariable long id){
        try{
            questionService.deleteQuestion(id);
            Response<String> response = new Response<>(false, HttpStatus.OK, "Question deleted successfully!", null);
			return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch(IllegalArgumentException ex){
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, ex.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
 
    //=============================================
    //============= Exam Module ===================
    //=============================================
    @GetMapping("/allExams")
    public ResponseEntity<List<Exam>> getAllExams() {
        try {
            List<Exam> exams = examService.getAllExams();
            return ResponseEntity.ok(exams);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
 
    @PostMapping("/createExam/{category}")
    public ResponseEntity<Response<?>> createExam(@PathVariable String category, @RequestBody ExamDTO examDTO){
        try{
            if( !questionService.categoryExists(category)  ){
                throw new Exception("The given category does not exist!");
            }

            if( examService.examNameExists(examDTO.getTitle()) ){
                throw new ExamExistsException("The given title already exists!");
            }
 
            if( examDTO.getDescription().isEmpty() || examDTO.getTitle().isEmpty() 
            || examDTO.getTotalMarks() == 0 || examDTO.getDuration() == 0 ){
                throw new MandatoryFieldMissingException("The necessary fields are missing!");
            }
 
            Exam exam = examService.createExam(category,examDTO);
            Response<Exam> response = new Response<Exam>(true, HttpStatus.CREATED, exam, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch(ExamExistsException ex){
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, "Error in creating the exam", ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        catch(MandatoryFieldMissingException ex){
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, "Error in creating the exam", ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }catch( Exception ex){
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, "Error in creating the exam", ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
 
    @PutMapping("/updateExam/{examId}")
    public ResponseEntity<Response<?>> updateExam(@PathVariable long examId , @RequestBody ExamDTO examDTO){
        try{
            if( !examService.examExists(examId)){
                throw new ExamNotFoundException("The exam with id "+ examId + "does not exists");
            }
 
            Exam exam = examService.updateExam(examId, examDTO);
            Response<Exam> response = new Response<Exam>(true, HttpStatus.ACCEPTED, exam, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch(ExamNotFoundException ex){
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getExam/{examId}")
    public ResponseEntity<Response<?>> getExamById(@PathVariable long examId){
        try{
            if( !examService.examExists(examId)){
                throw new ExamNotFoundException("The exam with id "+ examId + "does not exists");
            }
 
            Exam exam = examService.getExamById(examId);
            Response<Exam> response = new Response<Exam>(true, HttpStatus.OK, exam, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch(ExamNotFoundException ex){
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
 
    @DeleteMapping("/deleteExam/{examId}")
    public ResponseEntity<Response<?>> deleteExam(@PathVariable long examId){
        try{
            System.out.println("Exam with id "+ examId + " deletetion in progress");
            examService.deleteExam(examId);
            System.out.println("Exam with id "+ examId + " deleted successfully");
            Response<String> response = new Response<>(false, HttpStatus.OK, "Exam deleted successfully!", null);
			return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch(ExamNotFoundException ex){
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }   
 
    @PutMapping("updateQuestionsInExam/{examId}")
    public ResponseEntity<Response<String>> updateQuestionsInExam(@PathVariable long examId, @RequestBody List<QuestionDTO> questions) {
        try{ 
            examService.updateSelectedQuestions(examId, questions);
            Response<String> response = new Response<>(true, HttpStatus.ACCEPTED, "Questions updated successfully in the give Exam!", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch( ExamNotFoundException ex ){
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, "Exam not found!", ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/updateAQuestionInExam/{examId}")
    public ResponseEntity<Response<String>> updateAQuestionInExam(@PathVariable long examId ,
                                                                    @RequestParam String originalDescription,
                                                                    @RequestBody QuestionDTO updatedQuestionDTO){
        try{
            examService.updateAQuestionInExam( examId, originalDescription,updatedQuestionDTO);
            Response<String> response = new Response<>(true, HttpStatus.ACCEPTED, "Question updated successfully in the Exam!", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch(ExamNotFoundException ex){
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, "Exam not found!",null);
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }

    }
 
    //=============================================
    //=========== Response Module =================
    //=============================================
    @GetMapping("/evaluate/{examId}/{username}")
    public ResponseEntity<Response<?>> evaluateResponse(@PathVariable long examId, @PathVariable String username){
        try{
            Report report = examService.generateReport(examId,username);
            Response<Report> response = new Response<>(true, HttpStatus.OK, report, null);
			return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch(InvalidResponseException ex){
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/updateFeedback/{examId}/{username}")
    public ResponseEntity<Response<?>> updateFeedback(@PathVariable String username, @PathVariable long examId, @RequestBody String feedback) {
        try {
            Report report = reportService.updateReportFeedback(username, examId, feedback);            
            Response<Report> response = new Response<>(true, HttpStatus.OK, report, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } 
        catch (ReportNotFoundException ex) {
            Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // @DeleteMapping("/deleteReport/{examId}/{username}")
    // public ResponseEntity<Response<?>> deleteReport(@PathVariable long examId, @PathVariable String username) {
    //     try {
    //         reportService.deleteReport(username, examId);
    //         Response<String> response = new Response<>(true, HttpStatus.OK, "Report deleted successfully!", null);
    //         return new ResponseEntity<>(response, HttpStatus.OK);
    //     } 
    //     catch (ReportNotFoundException ex) {
    //         Response<String> response = new Response<>(false, HttpStatus.BAD_REQUEST, null, ex.getMessage());
    //         return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    //     }
    // }
 
      

}