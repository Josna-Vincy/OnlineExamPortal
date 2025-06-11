package com.cts.onlineexamportall.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reportId;

    private String userName;

    private long examId;

    private int correctAnswers;

    private int totalQuestions;

    private double score;

    private double percentage;

    private String feedback = "Feedback is yet to be assigned";
}
