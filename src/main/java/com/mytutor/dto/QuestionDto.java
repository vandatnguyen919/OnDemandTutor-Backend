/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto;

import com.mytutor.constants.QuestionStatus;
import com.mytutor.constants.RegexConsts;
import com.mytutor.entities.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Nguyen Van Dat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {

    private int id;

    private String title;

    private String content;

    private String createdAt;

    private String modifiedAt;

    private String questionUrl;

    private QuestionStatus status;
    
    private String subjectName;

    private ResponseAccountDetailsDto account;

    public static QuestionDto mapToDto(Question question, String subjectName) {
        if (question == null) {
            return null;
        }
        QuestionDto questionDto = new QuestionDto();
        questionDto.setId(question.getId());
        questionDto.setTitle(question.getTitle());
        questionDto.setContent(question.getContent());
        questionDto.setCreatedAt(RegexConsts.sdf.format(question.getCreatedAt()));
        questionDto.setModifiedAt(RegexConsts.sdf.format(question.getModifiedAt()));
        questionDto.setQuestionUrl(question.getQuestionUrl());
        questionDto.setStatus(question.getStatus());
        questionDto.setSubjectName(subjectName);
        questionDto.setAccount(ResponseAccountDetailsDto.mapToDto(question.getAccount()));

        return questionDto;

    }
}
