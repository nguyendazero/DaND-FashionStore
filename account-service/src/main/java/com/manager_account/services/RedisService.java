package com.manager_account.services;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface RedisService {
    public void saveValue(String key, Object value, long timeout) throws JsonProcessingException;
    public Object getValue(String key);
    public List<String> getList(String redisKey);
    public void addToList(String redisKey, String value);
    public void deleteKey(String key);
}
