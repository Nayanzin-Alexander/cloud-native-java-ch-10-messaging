package com.naynzin.stream.producer.channels;

import com.naynzin.stream.producer.ProducerChannels;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(ProducerChannels.class)
@SpringBootApplication
public class ChannelProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelProducerApplication.class, args);
    }
}
