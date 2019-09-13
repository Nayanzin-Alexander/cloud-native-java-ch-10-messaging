package com.naynzin.stream.producer;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

public interface SinkChannels {

    String BROADCAST_GREETING_SINK = "broadcastGreetingsSink";

    @Input(BROADCAST_GREETING_SINK)
    MessageChannel broadcastGreetingSink();
}
