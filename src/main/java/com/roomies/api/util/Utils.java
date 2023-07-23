package com.roomies.api.util;

import jakarta.servlet.http.HttpServletRequest;

public class Utils {
    public static String getRealIp(HttpServletRequest request){
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            // The X-Forwarded-For header can contain a comma-separated list of IPs
            // The first IP in the list is the original client IP
            String[] ipAddresses = forwardedFor.split(",");
            return ipAddresses[0].trim();
        } else {
            // If X-Forwarded-For is not available, check for X-Real-IP header
            String realIP = request.getHeader("X-Real-IP");
            if (realIP != null && !realIP.isEmpty()) {
                return realIP;
            } else {
                // If neither header is available, fallback to getting the IP directly from the request
                return request.getRemoteAddr();
            }
        }
    }
}
