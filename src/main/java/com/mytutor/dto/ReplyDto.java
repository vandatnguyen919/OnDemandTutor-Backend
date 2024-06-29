/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto;

import com.mytutor.constants.RegexConsts;
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
    private int createdById;
    private String createdBy;
    private String avatarUrl;
    private String content;
    private String createdAt;
    private String modifiedAt;

    public static ReplyDto mapToDto(Reply reply) {
        if (reply == null) {
            return null;
        }
        ReplyDto replyDto = new ReplyDto();
        replyDto.setId(reply.getId());
        replyDto.setCreatedById(reply.getCreatedBy().getId());
        replyDto.setCreatedBy(reply.getCreatedBy().getEmail());
        replyDto.setAvatarUrl(reply.getCreatedBy().getAvatarUrl());
        replyDto.setContent(reply.getContent());
        replyDto.setCreatedAt(RegexConsts.sdf.format(reply.getCreatedAt()));
        replyDto.setModifiedAt(RegexConsts.sdf.format(reply.getModifiedAt()));

        return replyDto;
    }
}
