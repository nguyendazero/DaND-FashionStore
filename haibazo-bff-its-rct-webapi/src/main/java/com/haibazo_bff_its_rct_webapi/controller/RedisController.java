package com.haibazo_bff_its_rct_webapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {

    private final RedisService redisService;

    @PostMapping("/set")
    public String setValue(@RequestParam String key, @RequestParam String value, @RequestParam(defaultValue = "3600") long timeout) throws JsonProcessingException {
        redisService.saveValue(key, value, timeout);
        return "Value set successfully!";
    }

    @GetMapping("/get")
    public APICustomize<?> getValue(@RequestParam String key) {
        Object value = redisService.getValue(key);
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), value);
    }
}