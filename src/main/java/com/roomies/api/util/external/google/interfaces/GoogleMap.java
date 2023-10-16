package com.roomies.api.util.external.google.interfaces;

import com.roomies.api.util.external.google.AddressResponse;
import com.roomies.api.util.external.google.Prediction;
import com.roomies.api.util.external.google.ValidationRequest;
import com.roomies.api.util.external.google.results.GeocodingResult;

import java.io.IOException;
import java.util.List;

public interface GoogleMap {
    AddressResponse validateAddress(ValidationRequest request) throws IOException;
    GeocodingResult lookupAddressByName(String address);
    GeocodingResult lookupAddressByCoords(Double longitude, Double latitude);
    List<Prediction> addressAutocompletion(String address);
    /**
     *
     * @param stringParts A String array
     * @return string - This string is built based on the string parts given in the argument
     */
    String buildSearchableString(String[] stringParts);
}
