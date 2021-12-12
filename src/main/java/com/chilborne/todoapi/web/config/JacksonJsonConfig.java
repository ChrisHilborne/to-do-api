package com.chilborne.todoapi.web.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.format.DateTimeFormatter;


import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE;

public class JacksonJsonConfig {

    private static final String dateFormat = "yyyy-MM-dd";
    private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);


    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            builder.simpleDateFormat(dateFormat);
            builder.serializers(new LocalDateTimeSerializer(dateTimeFormatter));
            builder.serializers(new LocalDateSerializer(dateFormatter));
            builder.propertyNamingStrategy(SNAKE_CASE);
            builder.indentOutput(true);
            builder.defaultViewInclusion(true);
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            builder.featuresToEnable(DeserializationFeature.USE_LONG_FOR_INTS);
            builder.featuresToEnable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return builder;
    }
}
