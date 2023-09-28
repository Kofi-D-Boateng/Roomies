package com.roomies.api.service;


import com.google.gson.Gson;
import com.roomies.api.model.request.SearchRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticService {

    private static final String VIEWERSHIP_TOPIC = "viewership_analytics";
    private static final String SEARCH_TOPIC = "search_analytics";

    @Autowired
    Gson gson;
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;


    private static class AnalyticWrapper{
        public String userId;
        public Object metadata;
        AnalyticWrapper(String id,Object data){
            userId = id;
            metadata = data;
        }
    }

    public void processViewership(@NonNull String userId,@NonNull Map<String,Object> objectMap){
        kafkaTemplate.send(VIEWERSHIP_TOPIC,gson.toJson(new AnalyticWrapper(userId,objectMap)));
    }

    public void processSearch(@NonNull String userId,@NonNull SearchRequest request){
        kafkaTemplate.send(SEARCH_TOPIC,gson.toJson(new AnalyticWrapper(userId,request)));
    }
}
