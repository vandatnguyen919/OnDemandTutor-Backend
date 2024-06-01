/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto;

import com.mytutor.entities.Reply;
import java.util.Date;
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
public class ReplyDto {

    private int id;
    private String content;
    private Date createdAt;
    private Date modifiedAt;
    private int createdById;

    public static ReplyDto mapToDto(Reply reply) {
        if (reply == null) {
            return null;
        }

        return new ReplyDto(
                reply.getId(),
                reply.getContent(),
                reply.getCreatedAt(),
                reply.getModifiedAt(),
                reply.getCreatedBy().getId()
        );
    }
}
