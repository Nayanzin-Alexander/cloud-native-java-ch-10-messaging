package com.naynzin.stream.producer.channels;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

@EnableBinding(Sink.class)
@Configuration
public class SinkConfiguration {

    @Bean
    @ServiceActivator(inputChannel = Sink.INPUT)
    public MessageHandler logHandler() {
        return (msg) ->
                System.out.println("Service Activator received message: " + msg);
    }
}
