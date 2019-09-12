package com.naynzin.stream.producer.channels;

import com.naynzin.stream.producer.ProducerChannels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class Controller {

    private final MessageChannel broadcastChannel;
    private final MessageChannel directChannel;

    @Autowired
    public Controller(ProducerChannels channels) {
        this.broadcastChannel = channels.broadcastGreetings();
        this.directChannel = channels.directGreetings();
    }

    @RequestMapping("/hi/{name}")
    ResponseEntity<String> hi(@PathVariable String name) {
        String message = "Hello, " + name;

        // Send message to channels.
        broadcastChannel.send(MessageBuilder.withPayload("Broadcast: " + message).build());
        directChannel.send(MessageBuilder.withPayload("Direct: " + message).build());

        return ok(message);
    }
}
