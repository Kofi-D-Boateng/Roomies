package com.roomies.api.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/search")
@Slf4j
public class SearchController {


    @GetMapping("/secured/find")
    public void find(@RequestParam("id") String id){
        System.out.println("id = " + id);
    }
}
