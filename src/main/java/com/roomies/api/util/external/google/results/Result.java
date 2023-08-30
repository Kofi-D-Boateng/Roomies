package com.roomies.api.util.external.google.results;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Result {
    @JsonProperty("address_components")
    private List<AddressComponent> AddressComponent;
    @JsonProperty("formatted_address")
    private String formattedAddress;
    private Geometry geometry;
    @JsonProperty("place_id")
    private String placeId;
    private List<String> types;
}
