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
    private Integer attempts = 0;
    private Integer exceededAttempts = 0;

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
        if(this.attempts > 1) this.attempts = 1;
        if(this.exceededAttempts > 0) this.exceededAttempts = 0;
        this.timestamp = newTimeStamp;
    }
}
