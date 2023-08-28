package com.roomies.api.controller;

import com.roomies.api.model.DTO.SearchDTO;
import com.roomies.api.service.SearchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/search/secured")
@Slf4j
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping("/autocomplete")
    public ResponseEntity<Object> autocompletion(@RequestParam("v") String val){
        if(val == null || val.trim().length() == 0) return ResponseEntity.status(201).body(null);
        log.info("Beginning autocompletion for {}",val);
        Object results = searchService.retrieveMatchedPatterns(val);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(results);
    }

    @GetMapping("/find-roommates")
    public void findRoommates(@ModelAttribute SearchDTO searchFields){

    }
}
