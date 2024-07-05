package com.mytutor.controllers;

import com.mytutor.dto.chat.MessageDto;
import com.mytutor.services.AccountService;
import com.mytutor.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public MessageDto receiveMessage(@Payload MessageDto messageDto) {
        System.out.println(messageDto);
//        return messageService.saveMessages(messageDto);
        return messageDto;
    }

    @MessageMapping("/private-message")
    public MessageDto recMessage(@Payload MessageDto messageDto) {
        System.out.println(messageDto);
        MessageDto savedMessage = messageService.saveMessages(messageDto);
        simpMessagingTemplate.convertAndSendToUser(messageDto.getReceiverId() + "", "/private", savedMessage);
        return savedMessage;
    }
}
