package com.roomies.api.util.external.google;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PredictionResponse {
    private List<Prediction> predictions;
    private String status;
}
