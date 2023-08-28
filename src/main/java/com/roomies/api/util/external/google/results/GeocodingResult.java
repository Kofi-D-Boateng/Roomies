package com.roomies.api.util.external.google.results;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GeocodingResult {
    private List<Result> results;
    private String status;
}
