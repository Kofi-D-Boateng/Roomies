package com.roomies.api.configuration.kafka;


import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfiguration {

    private static final String EMAIL_VERIFICATION_TOPIC = "email-verification";
    private static final String ROOM_GENERATION_TOPIC = "generate-room";
    private static final String ROOM_DESTRUCTION_TOPIC = "delete-room";
    public static String EMAIL_MULTIFACTOR = "email-multiFactor";
    public static String SMS_MULTIFACTOR = "sms-multiFactor";

    private static final String VIEWERSHIP_TOPIC = "viewership-analytics";
    private static final String SEARCH_TOPIC = "search-analytics";

    @Value("${spring.kafka.bootstrap.servers}")
    private String servers;
    public Map<String,Object> producerConfiguration(){
        Map<String,Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String,String> producerFactory(){
        return new DefaultKafkaProducerFactory<>(producerConfiguration());
    }

    @Bean
    public KafkaTemplate<String,String> kafkaTemplate(ProducerFactory<String,String> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic topic1(){
        return TopicBuilder.name(EMAIL_VERIFICATION_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topic2(){
        return TopicBuilder.name(ROOM_GENERATION_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topic3(){
        return TopicBuilder.name(ROOM_DESTRUCTION_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topic4(){
        return TopicBuilder.name(EMAIL_MULTIFACTOR)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topic5(){
        return TopicBuilder.name(SMS_MULTIFACTOR)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topic6(){
        return TopicBuilder.name(VIEWERSHIP_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topic7(){
        return TopicBuilder.name(SEARCH_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
