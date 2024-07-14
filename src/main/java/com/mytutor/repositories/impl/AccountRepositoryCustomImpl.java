package com.mytutor.repositories.impl;

import com.mytutor.constants.*;
import com.mytutor.entities.*;
import com.mytutor.repositories.AccountRepositoryCustom;
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
 * @author Nguyen Van Dat
 */
@Repository
public class AccountRepositoryCustomImpl implements AccountRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Account> findTutorsByFilters(Set<String> subjectSet,
                                             double priceMin,
                                             double priceMax,
                                             Set<DegreeType> tutorLevelSet,
                                             String sortBy,
                                             String keyword,
                                             List<AccountStatus> listOfStatus,
                                             Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> query = cb.createQuery(Account.class);
        Root<Account> account = query.from(Account.class);

        List<Predicate> predicates = new ArrayList<>();

        // Get all tutors
        predicates.add(account.get("role").in(Role.TUTOR));
        predicates.add(account.get("status").in(listOfStatus));

        // Filter by subjects
        if (subjectSet != null && !subjectSet.isEmpty()) {
            Join<Account, Subject> subjectJoin = account.join("subjects", JoinType.INNER);
            predicates.add(subjectJoin.get("subjectName").in(subjectSet));
        }

        // Filter by price
        Join<Account, TutorDetail> tutorDetailJoin = account.join("tutorDetail", JoinType.INNER);
        predicates.add(cb.greaterThanOrEqualTo(tutorDetailJoin.get("teachingPricePerHour"), priceMin));
        predicates.add(cb.lessThanOrEqualTo(tutorDetailJoin.get("teachingPricePerHour"), priceMax));

        //Filter by tutor level
        Join<Account, Education> educationJoin = account.join("educations", JoinType.LEFT);
        predicates.add(cb.equal(educationJoin.get("isVerified"), true));
        if (tutorLevelSet != null && !tutorLevelSet.isEmpty()) {
            predicates.add(educationJoin.get("degreeType").in(tutorLevelSet));
        }

        //Filter by keyword tutor's name or profile's description
        predicates.add(cb.or(
                cb.like(account.get("fullName"), "%" + keyword + "%")
//                cb.like(tutorDetailJoin.get("backgroundDescription"), "%" + keyword + "%")
        ));

        //Join with Feedback to get the rating
        Join<Account, Feedback> feedbackJoin = account.join("feedbacks", JoinType.LEFT);
        predicates.add(cb.or(
                cb.equal(feedbackJoin.get("type"), FeedbackType.REVIEW),
                cb.isNull(feedbackJoin.get("type"))
        ));

        Expression<Double> avgRating = cb.avg(feedbackJoin.get("rating"));

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        query.groupBy(account.get("id"));
        if ("rating".equalsIgnoreCase(sortBy)) {
            query.orderBy(cb.desc(avgRating));
        } else if ("price".equalsIgnoreCase(sortBy)) {
            query.orderBy(cb.desc(tutorDetailJoin.get("teachingPricePerHour")));
        }

        TypedQuery<Account> typedQuery = entityManager.createQuery(query);

        // Set pagination
        int totalRows = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Account> resultList = typedQuery.getResultList();
        return new PageImpl<>(resultList, pageable, totalRows);
    }
}
