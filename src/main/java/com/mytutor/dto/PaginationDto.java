package com.mytutor.dto;

import com.mytutor.dto.appointment.ResponseAppointmentDto;
import com.mytutor.dto.tutor.TutorInfoDto;
import com.mytutor.entities.Appointment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author vothimaihoa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationDto<T> {
    private List<T> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
