package com.mytutor.services.impl;

import com.mytutor.constants.MessageStatus;
import com.mytutor.dto.chat.MessageDto;
import com.mytutor.entities.Account;
import com.mytutor.entities.Message;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.MessageRepository;
import com.mytutor.services.MessageService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    private final AccountRepository accountRepository;

    public MessageServiceImpl(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public MessageDto saveMessages(MessageDto messageDto) {
        Account sender = accountRepository.findById(messageDto.getSenderId()).orElseThrow(() -> new AccountNotFoundException("Sender not found"));
        Account receiver = accountRepository.findById(messageDto.getReceiverId()).orElseThrow(() -> new AccountNotFoundException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMessage(messageDto.getMessage());
        message.setCreatedAt(new Date());
        message.setStatus(messageDto.getStatus());

        Message savedMessage = messageRepository.save(message);

        return MessageDto.mapToDto(savedMessage);
    }

    @Override
    public List<MessageDto> getMessages(Integer accountId) {
//        Account user = accountRepository.findByEmail(email).orElseThrow(() -> new AccountNotFoundException("Account not found"));
        Account user = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found"));
        List<Message> messages = messageRepository.findBySenderOrReceiverWithStatus(user, MessageStatus.MESSAGE);

        return messages.stream().map(MessageDto::mapToDto).toList();
    }
}
