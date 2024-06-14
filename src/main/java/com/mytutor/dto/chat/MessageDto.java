package com.mytutor.dto.chat;

import com.mytutor.constants.MessageStatus;
import com.mytutor.constants.RegexConsts;
import com.mytutor.entities.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private Long id;
    private Integer senderId;
    private String senderEmail;
    private Integer receiverId;
    private String receiverEmail;
    private String message;
    private String createdDate;
    private MessageStatus status;

    public static MessageDto mapToDto(Message message) {

        MessageDto messageDto = new MessageDto();
        messageDto.setId(message.getId());
        messageDto.setSenderId(message.getSender().getId());
        messageDto.setSenderEmail(message.getSender().getEmail());
        if (message.getReceiver() != null) {
            messageDto.setReceiverId(message.getReceiver().getId());
            messageDto.setReceiverEmail(message.getReceiver().getEmail());
        }
        messageDto.setMessage(message.getMessage());
        messageDto.setCreatedDate(RegexConsts.sdf.format(message.getCreatedDate()));
        messageDto.setStatus(message.getStatus());

        return messageDto;
    }
}
