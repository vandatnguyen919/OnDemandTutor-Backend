/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mytutor.dto.timeslot;

import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Nguyen Van Dat
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScheduleItemDto {

    private String dayOfWeek;
    
    private int dayOfMonth;
    
    private List<TimeslotDto> timeslots = new LinkedList<>();
}
