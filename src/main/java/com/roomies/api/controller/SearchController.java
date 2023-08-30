package com.roomies.api.controller;

import com.roomies.api.enums.ServiceResponse;
import com.roomies.api.model.DTO.MaskedRoommateDTO;
import com.roomies.api.model.request.SearchRequest;
import com.roomies.api.service.SearchService;
import com.roomies.api.util.custom.ResponseTuple;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/search/secured")
@Slf4j
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping("/autocomplete")
    public ResponseEntity<Set<String>> autocompletion(@RequestParam("v") String val){
        if(val == null || val.trim().length() == 0) return ResponseEntity.status(201).body(null);
        log.info("Beginning autocompletion for {}",val);
        Set<String> results = searchService.retrieveMatchedPatterns(val);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(results);
    }

    @GetMapping("/find-roommates")
    public ResponseEntity<Set<MaskedRoommateDTO>> findRoommates(@ModelAttribute SearchRequest searchFields){
        log.info("Beginning search request for {}",searchFields);
        if(searchFields.getAddress().trim().length() == 0) return ResponseEntity.status(400).body(null);
        ResponseTuple<ServiceResponse,Set<MaskedRoommateDTO>,Object> result = searchService.findRoommates(searchFields);
        if(!result.getVal1().equals(ServiceResponse.SUCCESSFUL)) return ResponseEntity.status(500).body(null);
        return ResponseEntity.status(200).body(result.getVal2());
    }
}
