package com.cts.onlineexamportall.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cts.onlineexamportall.dao.ReportDAO;
import com.cts.onlineexamportall.exception.ReportNotFoundException;
import com.cts.onlineexamportall.model.Report;

@Service
public class ReportService {

    private static final Logger logger = LogManager.getLogger(ReportService.class);

    @Autowired
    private ReportDAO reportDAO;

    public List<Report> getAllReports() {
        logger.info("Fetching all reports");
        List<Report> reports = reportDAO.findAll();
        if (reports.isEmpty()) {
            logger.warn("No reports found");
            throw new ReportNotFoundException("No reports found");
        }
        logger.info("Reports found");
        return reports;
    }

    public Report updateReportFeedback(String userName, long examId, String feedback) {
        try{ 
            Report report = reportDAO.findByUserNameAndExamId(userName, examId);
            logger.info("Report found for the given userName and examId");
            report.setFeedback(feedback);
            return reportDAO.save(report);
        }catch(ReportNotFoundException e){
            logger.error("Report not found for the given userName and examId");
            throw new ReportNotFoundException("Report not found for the given userName and examId");
        }
    }

    public List<Report> getReportsByUserName(String username) {
        logger.info("Fetching reports for username: {}", username);
        try {
            List<Report> report = reportDAO.findByUserName(username);
            if (report.isEmpty()) {
                logger.warn("No reports found for username: {}", username);
                throw new ReportNotFoundException("Report for the username " + username + " not found");
            }
            logger.info("Reports found for username: {}", username);
            return report;
        } catch (ReportNotFoundException ex) {
            logger.error("Report not found for username: {}", username);
            throw new ReportNotFoundException("Report for the username " + username + " not found");
        }
    }

   
}