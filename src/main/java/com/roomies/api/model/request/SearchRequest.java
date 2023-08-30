package com.roomies.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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
    private boolean violationPresent;

    public String generateHashKey(){
        String[] fullName = this.getClass().getName().split("//.");
        String className = fullName[fullName.length-1];
        return className + this.address + " " + distance;
    }

    public void validationCheck(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchRequest that = (SearchRequest) o;
        return Double.compare(that.getDistance(), getDistance()) == 0 && isStudentOnly() == that.isStudentOnly() && Objects.equals(getAddress(), that.getAddress()) && Objects.equals(getAvailabilityDate(), that.getAvailabilityDate()) && Objects.equals(getMaximumPayableRent(), that.getMaximumPayableRent()) && Objects.equals(getMinimumPayableRent(), that.getMinimumPayableRent()) && Objects.equals(getLocale(), that.getLocale());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddress(), getAvailabilityDate(), getMaximumPayableRent(), getMinimumPayableRent(), getDistance(), isStudentOnly(), getLocale());
    }
}
