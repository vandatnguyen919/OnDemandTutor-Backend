package com.mytutor.dto.moderator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCheckTutorDto {
    private List<String> approvedSubjects;
    private List<Integer> approvedEducations;
    private List<Integer> approvedCertificates;
    private String backgroundDescription;
    private String videoIntroductionLink;

}
