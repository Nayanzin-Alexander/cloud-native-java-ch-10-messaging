package com.naynzin.stream.producer.gateway;

import com.naynzin.stream.producer.ProducerChannels;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(ProducerChannels.class)
@SpringBootApplication
public class GatewayProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayProducerApplication.class, args);
    }


    @StreamListener(ProducerChannels.BROADCAST)
    public void handleBroadcast(String msg) {
        System.out.println(msg);
    }

}
