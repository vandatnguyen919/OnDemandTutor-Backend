package com.mytutor.controllers;

import com.mytutor.dto.chat.MessageDto;
import com.mytutor.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<List<MessageDto>> getMessagesByAccountId(@PathVariable Integer accountId) {
        List<MessageDto> messageDtos = messageService.getMessages(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(messageDtos);
    }
}
