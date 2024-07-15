package com.mytutor.services;

import java.util.Map;

public interface ExrateService {

    Map getExrates();

    Map<String, Object> getExrateByCurrencyCode(String currencyCode);
}