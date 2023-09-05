package com.roomies.api.util.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.roomies.api.model.DTO.MaskedRoommateDTO;
import com.roomies.api.model.DTO.RoommateRequestDTO;
import com.roomies.api.model.roommate.RoommateRequest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RequestConverter extends StdConverter<RoommateRequest, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convert(RoommateRequest request) {

//        RoommateRequestDTO requestDTO = new RoommateRequestDTO();
//        MaskedRoommateDTO r1 = objectMapper.convertValue(request.getRequestedRoommate(), MaskedRoommateDTO.class);
//        MaskedRoommateDTO r2 = objectMapper.convertValue(request.getRequestingRoommate(), MaskedRoommateDTO.class);
//        requestDTO.setRequestedRoommate(r1);
//        requestDTO.setRequestingRoommate(r2);
//        requestDTO.setId(request.getId());
//        requestDTO.setAcceptedRequest(request.getAcceptedRequest());
//        requestDTO.setMessage(request.getMessage());
//        requestDTO.setCreationTimestamp(request.getCreationTimestamp());
//        requestDTO.setAcceptedRequest(request.getAcceptedRequest());
//        requestDTO.setRejectionTimestamp(request.getRejectionTimestamp());
//        return requestDTO.toString();
        return objectMapper.convertValue(request,RoommateRequestDTO.class).toString();
//        return roommateRequests.stream().map(
//                request -> {
//                    RoommateRequestDTO requestDTO = new RoommateRequestDTO();
//                    MaskedRoommateDTO r1 = objectMapper.convertValue(request.getRequestedRoommate(), MaskedRoommateDTO.class);
//                    MaskedRoommateDTO r2 = objectMapper.convertValue(request.getRequestingRoommate(), MaskedRoommateDTO.class);
//                    requestDTO.setRequestedRoommate(r1);
//                    requestDTO.setRequestingRoommate(r2);
//                    requestDTO.setId(request.getId());
//                    requestDTO.setAcceptedRequest(request.getAcceptedRequest());
//                    requestDTO.setMessage(request.getMessage());
//                    requestDTO.setCreationTimestamp(request.getCreationTimestamp());
//                    requestDTO.setAcceptedRequest(request.getAcceptedRequest());
//                    requestDTO.setRejectionTimestamp(request.getRejectionTimestamp());
////                    return objectMapper.convertValue(request, RoommateRequestDTO.class);
//                    return requestDTO;
//                }
//        ).map(RoommateRequestDTO::toString).collect(Collectors.toSet());
    }
}
