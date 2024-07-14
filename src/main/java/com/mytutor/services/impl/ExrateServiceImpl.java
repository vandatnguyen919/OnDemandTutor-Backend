package com.mytutor.services.impl;

import com.mytutor.services.ExrateService;
import jakarta.validation.constraints.NotNull;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ExrateServiceImpl implements ExrateService {

    @Value("${vietcombank.exrateApiUrl}")
    private String url;

    @Override
    public Map getExrates() {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        assert response != null;
        return XML.toJSONObject(response).toMap();
    }

    @Override
    public Map<String, Object> getExrateByCurrencyCode(@NotNull String currencyCode) {
        Map<String, Object> exrateList = (Map<String, Object>) getExrates().get("ExrateList");
        List<Map<String, Object>> exrate = (List<Map<String, Object>>) exrateList.get("Exrate");
        return exrate.stream()
                .filter(entry -> currencyCode.equals(entry.get("CurrencyCode")))
                .findFirst()
                .orElse(null);
    }
}
