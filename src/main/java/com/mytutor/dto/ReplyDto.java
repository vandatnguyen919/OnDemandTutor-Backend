/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto;

import com.mytutor.entities.Reply;
import java.text.SimpleDateFormat;
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
    private String createdAt;
    private String modifiedAt;

    private int createdBy;
    public static ReplyDto mapToDto(Reply reply) {
        if (reply == null) {
            return null;
        }


        ReplyDto replyDto = new ReplyDto();
        replyDto.setId(reply.getId());
        replyDto.setContent(reply.getContent());
        
        // Create a SimpleDateFormat object with the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        replyDto.setCreatedAt(sdf.format(reply.getCreatedAt()));
        replyDto.setModifiedAt(sdf.format(reply.getModifiedAt()));
        replyDto.setCreatedBy(reply.getCreatedBy().getId());
        
        return replyDto;
    }
}
