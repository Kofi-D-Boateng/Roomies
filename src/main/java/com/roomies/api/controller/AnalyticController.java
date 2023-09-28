package com.roomies.api.controller;

import com.roomies.api.service.AnalyticService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/analytics/secured")
@Slf4j
public class AnalyticController {

    @Autowired
    AnalyticService analyticService;

    @PostMapping("/viewership")
    public void viewership(@RequestParam("id") String userId,@RequestBody Map<String,Object> objectMap){
        analyticService.processViewership(userId, objectMap);
    }
}
