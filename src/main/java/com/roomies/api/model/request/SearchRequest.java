package com.roomies.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchRequest {
    private String address;
    private Long availabilityDate;
    private Double maximumPayableRent;
    private Double minimumPayableRent;
    private double distance = 25;
    private boolean studentOnly;
    private String locale;

    public String generateHashKey(){
        String[] fullName = this.getClass().getName().split("//.");
        String className = fullName[fullName.length-1];
        return className + this.address + distance;
    }
}
