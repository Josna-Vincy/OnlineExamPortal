package com.cts.onlineexamportall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class ExamDTO {
    
    private String title;
    private String description;
    private int totalMarks;
    private int duration;
}
