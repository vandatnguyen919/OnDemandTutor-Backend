package com.mytutor.repositories.impl;

import com.mytutor.constants.QuestionStatus;
import com.mytutor.entities.Account;
import com.mytutor.entities.Question;
import com.mytutor.entities.Subject;
import com.mytutor.repositories.QuestionRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author vothimaihoa
 */
@Repository
public class QuestionRepositoryCustomImpl implements QuestionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Question> findQuestionsByFilter(Account student, QuestionStatus status, Set<String> subjectSet, String questionContent, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Question> query = cb.createQuery(Question.class);
        Root<Question> question = query.from(Question.class);

        List<Predicate> predicates = new ArrayList<>();

        if (status != null) {
            predicates.add(cb.equal(question.get("status"), status));
        }

        if (student != null) {
            predicates.add(cb.equal(question.get("account"), student));
        }

        if (subjectSet != null && !subjectSet.isEmpty()) {
            Join<Question, Subject> subjectJoin = question.join("subject", JoinType.INNER);
            predicates.add(subjectJoin.get("subjectName").in(subjectSet));
        }

        if (questionContent != null && !questionContent.isEmpty()) {
            String likePattern = "%" + questionContent + "%";
            Predicate contentPredicate = cb.or(
                    cb.like(question.get("title"), likePattern),
                    cb.like(question.get("content"), likePattern)
            );
            predicates.add(contentPredicate);
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Question> typedQuery = entityManager.createQuery(query);
        int totalRows = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Question> resultList = typedQuery.getResultList();
        return new PageImpl<>(resultList, pageable, totalRows);
    }
}
