package com.roomies.api.model.session;

import com.roomies.api.interfaces.RequestLimitingContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestContext implements RequestLimitingContext {
    private Long timestamp;
    private Integer attempts;
    private Integer exceededAttempts;
    private Integer LoginAttempts;
    private Long blockedTimeStamp;

    @Override
    public void incrementAttempts() {
        this.attempts++;
    }

    @Override
    public void incrementExceededAttempts() {
        this.exceededAttempts++;

    }

    @Override
    public void resetContext(Long newTimeStamp) {
        this.attempts = 1;
        this.exceededAttempts = 0;
        this.timestamp = newTimeStamp;
    }

    @Override
    public void incrementLoginAttempts() {
        this.exceededAttempts++;
    }

    @Override
    public void resetBlockedTimeStamp() {

    }
}
