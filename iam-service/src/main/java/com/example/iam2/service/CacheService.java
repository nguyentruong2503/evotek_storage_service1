package com.example.iam2.service;

public interface CacheService {

    void set (String key, Object value, Long ttlSeconds);

    <T> T get(String key, Class<T> type);

    void delete(String key);

    void deleteByPattern(String pattern);
}
