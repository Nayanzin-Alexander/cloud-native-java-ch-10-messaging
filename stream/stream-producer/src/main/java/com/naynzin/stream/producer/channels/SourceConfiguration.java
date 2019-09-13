package com.naynzin.stream.producer.channels;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;

import java.util.concurrent.atomic.AtomicBoolean;

@EnableBinding(Source.class)
@Configuration
public class SourceConfiguration {

    private static final AtomicBoolean semaphore = new AtomicBoolean(true);

    @Bean
    @InboundChannelAdapter(channel = Source.OUTPUT, poller = @Poller(fixedDelay = "100"))
    public MessageSource<String> fooBarSource() {
        return () -> {
            String msg = semaphore.getAndSet(!semaphore.get()) ? "foo" : "bar";
            return new GenericMessage<>(msg);
        };
    }

}
