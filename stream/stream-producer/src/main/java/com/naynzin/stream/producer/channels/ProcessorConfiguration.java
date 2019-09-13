package com.naynzin.stream.producer.channels;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.SendTo;

@EnableBinding(Processor.class)
@Configuration
public class ProcessorConfiguration {

    @StreamListener(Processor.INPUT)
    @SendTo(Processor.OUTPUT)
    public String transform(String message) {
        return message.toUpperCase();
    }
}
