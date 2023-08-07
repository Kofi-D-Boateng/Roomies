package com.roomies.api.model.geolocation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GeolocationRequest {
    private String ip;
    private String format;
    private String countryCode;
    private GeolocationRequest(Builder builder){
        ip = builder.ip;
        format = builder.format;
        countryCode = builder.countryCode;
    }

    public static class Builder{
        private String ip;
        private String format;
        private String countryCode;

        public Builder withIp(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder withFormat(String format) {
            this.format = format;
            return this;
        }

        public Builder withCountryCode(String code) {
            this.countryCode = code;
            return this;
        }

        public GeolocationRequest build(){
            return new GeolocationRequest(this);
        }
    }
}


