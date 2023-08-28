package com.roomies.api.util.external.google;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Address {
    private String formattedAddress;
    private PostalAddress postalAddress;

    public static class PostalAddress {
        private String regionCode;
        private String languageCode;
        private String administrativeArea;
        private String locality;
        private String[] addressLines;
    }
}
