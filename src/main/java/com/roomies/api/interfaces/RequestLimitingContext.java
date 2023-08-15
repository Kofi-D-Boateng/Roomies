package com.roomies.api.interfaces;

public interface RequestLimitingContext {
    void incrementAttempts();
    void incrementExceededAttempts();
    void incrementLoginAttempts();
    void resetBlockedTimeStamp();
    void resetContext(Long timestamp);
}
