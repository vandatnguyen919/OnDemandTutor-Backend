package com.mytutor.services;

import com.mytutor.dto.chat.MessageDto;

import java.util.List;

public interface MessageService {

    MessageDto saveMessages(MessageDto messageDto);

    List<MessageDto> getMessages(String username);
}
