package com.mytutor.repositories;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.DegreeType;
import com.mytutor.entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface AccountRepositoryCustom {

    Page<Account> findTutorsByFilters(Set<String> subjectSet,
                                      double priceMin,
                                      double priceMax,
                                      Set<DegreeType> tutorLevelSet,
                                      String sortBy,
                                      String keyword,
                                      List<AccountStatus> listOfStatus,
                                      Pageable pageable);
}
