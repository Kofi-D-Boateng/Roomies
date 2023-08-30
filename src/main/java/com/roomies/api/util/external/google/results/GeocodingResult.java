package com.roomies.api.util.external.google.results;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GeocodingResult implements Serializable {
    private static final long serializableId = 6466425627L;
    private List<Result> results;
    private String status;
}
