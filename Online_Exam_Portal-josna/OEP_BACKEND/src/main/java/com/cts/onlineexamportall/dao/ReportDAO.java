package com.cts.onlineexamportall.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.onlineexamportall.model.Report;

@Repository
public interface ReportDAO extends JpaRepository<Report, Long> {

    List<Report> findByUserName(String username);

    Report findByUserNameAndExamId(String userName, long examId);

    List<Report> findByExamId(long examId);

    Optional<Report> findByExamIdAndUserName(Long examId, String userName);


}