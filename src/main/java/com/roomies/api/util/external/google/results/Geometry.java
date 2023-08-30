package com.roomies.api.util.external.google.results;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Geometry {
    private Location location;
    @JsonProperty("location_type")
    private String LocationType;
    private Viewport viewport;
}
