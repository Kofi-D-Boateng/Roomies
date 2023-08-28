package com.roomies.api.util.parsing;

import com.roomies.api.util.external.google.AddressResponse;
import com.roomies.api.util.external.google.Prediction;
import com.roomies.api.util.external.google.results.GeocodingResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface GParser {

    List<Prediction> parsePredictions(InputStream inputStream) throws IOException;

    AddressResponse parseAddressResponse(InputStream inputStream);
    GeocodingResult parseGeocodeResults(InputStream inputStream) throws IOException;


}
