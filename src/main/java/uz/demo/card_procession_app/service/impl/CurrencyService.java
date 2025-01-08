package uz.demo.card_procession_app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.demo.card_procession_app.entity.data.CurrencyRate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private static final String CURRENCY_API_URL = "https://cbu.uz/ru/arkhiv-kursov-valyut/json/";
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_KEY = "currency_id_69";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public Long getCurrencyRateById()  {
        Long rate = (Long) redisTemplate.opsForValue().get(CACHE_KEY);
        if (rate != null && rate != 0) {
            return rate;
        }

        String jsonResponse = restTemplate.getForObject(CURRENCY_API_URL, String.class);
        if (!Objects.isNull(jsonResponse)) {
            List<CurrencyRate> currencyRates;
            try {
                currencyRates = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                return null;
            }

            Long newRate = Math.round(currencyRates.get(0).getRate());
            redisTemplate.opsForValue().set(CACHE_KEY, newRate);
            redisTemplate.expire(CACHE_KEY, 24, java.util.concurrent.TimeUnit.HOURS); // Set expiry to 1 day
            return rate;
        }
        return null;
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void refreshCurrencyCacheRate() {
        getCurrencyRateById();
    }
}


