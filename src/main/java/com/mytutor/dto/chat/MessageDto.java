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
    private String senderFullName;
    private String senderAvatarUrl;
    private Integer receiverId;
    private String receiverEmail;
    private String receiverFullName;
    private String receiverAvatarUrl;
    private String message;
    private String createdAt;
    private MessageStatus status;

    public static MessageDto mapToDto(Message message) {

        MessageDto messageDto = new MessageDto();
        messageDto.setId(message.getId());
        // Sender info
        messageDto.setSenderId(message.getSender().getId());
        messageDto.setSenderEmail(message.getSender().getEmail());
        messageDto.setSenderFullName(message.getSender().getFullName());
        messageDto.setSenderAvatarUrl(message.getSender().getAvatarUrl());
        // Receiver info
        if (message.getReceiver() != null) {
            messageDto.setReceiverId(message.getReceiver().getId());
            messageDto.setReceiverEmail(message.getReceiver().getEmail());
            messageDto.setReceiverFullName(message.getReceiver().getFullName());
            messageDto.setReceiverAvatarUrl(message.getReceiver().getAvatarUrl());
        }
        // Message info
        messageDto.setMessage(message.getMessage());
        messageDto.setCreatedAt(RegexConsts.sdf.format(message.getCreatedAt()));
        messageDto.setStatus(message.getStatus());

        return messageDto;
    }
}
