package com.roomies.api.service.interfaces;

import com.roomies.api.enums.RateLimitStatus;
import jakarta.servlet.http.HttpServletRequest;

public interface Limiter {
    void resetSessionLimit(HttpServletRequest request);
    RateLimitStatus checkForAcceptableRequest(HttpServletRequest request);
    boolean checkForBlockedIp(String ip);
    void blockIp(String ip, String userAgent);

    void clearRequestContext(String ip);

    void clearAllRequestContext();
}
