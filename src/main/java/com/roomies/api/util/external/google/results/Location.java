package com.roomies.api.util.external.google.results;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Location {
    @JsonProperty("lat")
    private double latitude;
    @JsonProperty("lng")
    private double longitude;

}
