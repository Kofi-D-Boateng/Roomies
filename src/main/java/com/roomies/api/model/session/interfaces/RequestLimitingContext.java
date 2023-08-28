package com.roomies.api.model.session.interfaces;

public interface RequestLimitingContext {
    void incrementAttempts();
    void incrementExceededAttempts();
    void incrementLoginAttempts();
    void resetBlockedTimeStamp();
    void resetContext(Long timestamp);
}
