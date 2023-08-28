package com.roomies.api.util.external.google;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValidationRequest {
    private AddressParts addressParts;


    private static class AddressParts{
        private String regionCode;
        private String locality;
        private String[] addressLines;
    }
}
