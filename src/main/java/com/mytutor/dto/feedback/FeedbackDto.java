/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto.feedback;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mytutor.constants.FeedbackType;
import com.mytutor.constants.RegexConsts;
import com.mytutor.dto.ReplyDto;
import com.mytutor.entities.Feedback;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
public class FeedbackDto {

    private int id;
    private int createdById;
    private String createdBy;
    private String avatarUrl;
    private int tutorId;
    private Integer rating;
    private String content;
    private String createdAt;
    private String modifiedAt;
    private Boolean isBanned;
    private FeedbackType type;
    @JsonIgnore
    private List<ReplyDto> replies = new ArrayList<>();

    public static FeedbackDto mapToDto(Feedback feedback) {
        if (feedback == null) {
            return null;
        }

        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setId(feedback.getId());
        feedbackDto.setCreatedById(feedback.getCreatedBy().getId());
        feedbackDto.setCreatedBy(feedback.getCreatedBy().getEmail());
        feedbackDto.setAvatarUrl(feedback.getCreatedBy().getAvatarUrl());
        feedbackDto.setTutorId(feedback.getTutor().getId());
        feedbackDto.setRating(feedback.getRating());
        feedbackDto.setContent(feedback.getContent());
        
        feedbackDto.setCreatedAt(RegexConsts.sdf.format(feedback.getCreatedAt()));
        feedbackDto.setModifiedAt(RegexConsts.sdf.format(feedback.getModifiedAt()));
        feedbackDto.setIsBanned(feedback.getIsBanned());
        feedbackDto.setType(feedback.getType());
        feedbackDto.setReplies(feedback.getReplies().stream().map(ReplyDto::mapToDto).collect(Collectors.toList()));

        return feedbackDto;
    }
}
