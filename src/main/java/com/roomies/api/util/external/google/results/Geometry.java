package com.roomies.api.util.external.google.results;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Geometry {
    private Location location;
    private String location_type;
    private Viewport viewport;
}
