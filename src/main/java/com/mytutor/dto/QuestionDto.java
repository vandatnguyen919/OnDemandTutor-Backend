/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto;

import com.mytutor.constants.QuestionStatus;
import com.mytutor.entities.Question;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Nguyen Van Dat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDto {

    private int id;

    private String content;

    private Date createdAt;

    private Date modifiedAt;

    private String questionUrl;

    private QuestionStatus status;

    public static QuestionDto mapToDto(Question question) {
        if (question == null) {
            return null;
        }
        return QuestionDto.builder()
                .id(question.getId())
                .content(question.getContent())
                .createdAt(question.getCreatedAt())
                .modifiedAt(question.getModifiedAt())
                .questionUrl(question.getQuestionUrl())
                .status(question.getStatus())
                .build();
    }
}
