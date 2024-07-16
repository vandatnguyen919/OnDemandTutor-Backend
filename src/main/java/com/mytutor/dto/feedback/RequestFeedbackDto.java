package com.mytutor.dto.feedback;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RequestFeedbackDto {

    @Min(value = 1, message = "must be at least 1 star")
    @Max(value = 5, message = "must be at most 5 stars")
    private Float rating;

    @Size(min = 1, max = 1000, message = "must be between 1 and 1000 characters")
    private String content;

    @JsonIgnore
    private Boolean isBanned;
}
