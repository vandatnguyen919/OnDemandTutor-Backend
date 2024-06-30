package com.mytutor.repositories;

import com.mytutor.constants.MessageStatus;
import com.mytutor.entities.Account;
import com.mytutor.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE (m.sender = :account OR m.receiver = :account) AND m.status = :status")
    List<Message> findBySenderOrReceiverWithStatus(@Param("account") Account account, @Param("status") MessageStatus messageStatus);
}
