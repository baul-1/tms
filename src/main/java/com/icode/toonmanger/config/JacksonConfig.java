package com.icode.toonmanger.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.ArrayList;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper jsonObjectMapper() {
        SimpleModule m = new SimpleModule();
        m.addDeserializer(CMap.class,new CMapDeserializer());
        m.addSerializer(CMap.class,new CMapSerializer());
        m.addSerializer(CResult.class,new CResultSerializer());

        return Jackson2ObjectMapperBuilder.json()
                .modules(m)
                .build();
    }
}