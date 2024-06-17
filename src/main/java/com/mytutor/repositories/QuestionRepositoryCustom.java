package com.mytutor.repositories;

import com.mytutor.constants.QuestionStatus;
import com.mytutor.entities.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

/**
 *
 * @author vothimaihoa
 */
public interface QuestionRepositoryCustom {
    Page<Question> findQuestionsByFilter(QuestionStatus status,
                                          Set<String> subjectSet,
                                          String questionContent,
                                          Pageable pageable);
}
