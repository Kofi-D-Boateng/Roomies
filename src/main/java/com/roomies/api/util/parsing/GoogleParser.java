package com.roomies.api.util.parsing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomies.api.util.external.google.AddressResponse;
import com.roomies.api.util.external.google.Prediction;
import com.roomies.api.util.external.google.PredictionResponse;
import com.roomies.api.util.external.google.results.GeocodingResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
public class GoogleParser implements GParser {
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public List<Prediction> parsePredictions(InputStream inputStream) throws IOException {
        log.info("Request was successful.... beginning mapping to proper class....");
        try {
            PredictionResponse predictionResponse = objectMapper.readValue(inputStream, PredictionResponse.class);
            log.info("{}",predictionResponse.getStatus());
            return predictionResponse.getPredictions();
        }catch (IOException e){
            log.trace("Error reading bytes or converting to class: {}", PredictionResponse.class.getName());
            return null;
        }
    }

    @Override
    public AddressResponse parseAddressResponse(InputStream inputStream) {
        log.info("Request was successful.... beginning mapping to proper class....");
        return objectMapper.convertValue(inputStream, AddressResponse.class);
    }

    @Override
    public GeocodingResult parseGeocodeResults(InputStream inputStream) throws IOException {
        log.info("Request was successful.... beginning mapping to proper class....");
        try {
            return objectMapper.readValue(inputStream, GeocodingResult.class);
        }catch (IOException e){
            log.warn("Error trying to convert stream into {}\nError: {}",GeocodingResult.class.getName(),e);
            return null;
        }
    }
}
