package com.mytutor.dto.moderator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCheckDocumentDto {
    private List<Integer> approvedEducations;
    private List<Integer> approvedCertificates;
}
