package com.mytutor.dto.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RequestQuestionDto {

    private String title;

    private String content;

    private String questionUrl;

    private String subjectName;
}
