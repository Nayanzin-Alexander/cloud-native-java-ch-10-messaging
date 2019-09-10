package com.nayanzin.integration.processfiles;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;

@Configuration
public class ChannelsConfiguration {

    @Bean
    MessageChannel invalid() {
        return MessageChannels
                .direct()
                .get();
    }

    @Bean
    MessageChannel completed() {
        return MessageChannels
                .direct()
                .get();
    }
}
