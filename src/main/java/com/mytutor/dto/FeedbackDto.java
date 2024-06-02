/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto;

import com.mytutor.constants.FeedbackType;
import com.mytutor.entities.Feedback;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private int createdBy;
    private int tutorId;
    private int rating;
    private String content;
    private Date createdAt;
    private Date modifiedAt;
    private boolean isBanned;
    private FeedbackType type;
    private List<ReplyDto> replies = new ArrayList<>();

    public static FeedbackDto mapToDto(Feedback feedback) {
        if (feedback == null) {
            return null;
        }

        return new FeedbackDto(
                feedback.getId(),
                feedback.getCreateBy().getId(),
                feedback.getTutor().getId(),
                feedback.getRating(),
                feedback.getContent(),
                feedback.getCreatedAt(),
                feedback.getModifiedAt(),
                feedback.isBanned(),
                feedback.getType(),
                feedback.getReplies().stream().map(r -> ReplyDto.mapToDto(r)).toList()
        );
    }
}
