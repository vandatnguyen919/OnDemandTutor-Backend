/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.repositories;

import com.mytutor.entities.Feedback;
import com.mytutor.entities.Reply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Nguyen Van Dat
 */
public interface ReplyRepository extends JpaRepository<Reply, Integer> {

}
