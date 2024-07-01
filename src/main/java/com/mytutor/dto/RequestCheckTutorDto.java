package com.mytutor.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCheckTutorDto {
    private List<Integer> approvedSubjects;
    private List<Integer> approvedEducations;
    private List<Integer> approvedCertificates;
    private String backgroundDescription;
    private String videoIntroductionLink;

}
