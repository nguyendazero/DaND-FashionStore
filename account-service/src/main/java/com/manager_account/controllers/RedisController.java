package com.manager_account.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.manager_account.entities.Account;
import com.manager_account.services.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getValue(@RequestParam String key) {
        Object value = redisService.getValue(key);
        return ResponseEntity.ok(value);
    }

}
