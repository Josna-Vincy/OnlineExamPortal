package com.cts.onlineexamportall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class QuestionDTO {
    private String description;
    private String category;
    private String difficulty;
    private String correctAnswer;

}
