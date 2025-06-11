package com.cts.onlineexamportall.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class ExamResponseDTO {
    private long examId;
    private String userName;

    private List<ExamAnswersDTO> answers;
}
