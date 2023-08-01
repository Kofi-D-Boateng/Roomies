package com.roomies.api.interfaces;

public interface RequestLimitingContext {
    void incrementAttempts();
    void incrementExceededAttempts();
    void resetContext(Long timestamp);
}
