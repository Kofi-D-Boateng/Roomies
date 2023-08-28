package com.roomies.api.util.external.google.results;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Viewport {
    private Location northeast;
    private Location southwest;
}
