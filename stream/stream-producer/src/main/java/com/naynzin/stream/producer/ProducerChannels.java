package com.naynzin.stream.producer;


import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ProducerChannels {

    String BROADCAST_GREETINGS = "broadcastGreetings";
    String DIRECT_GREETINGS = "directGreetings";

    @Output(BROADCAST_GREETINGS)
    MessageChannel broadcastChannel();


    @Output(DIRECT_GREETINGS)
    MessageChannel directChannel();
}
