package com.naynzin.stream.producer.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class Controller {

    private final GreetingGateway greetingGateway;

    @Autowired
    public Controller(GreetingGateway greetingGateway) {
        this.greetingGateway = greetingGateway;
    }

    @RequestMapping("/hi/{name}")
    ResponseEntity<String> hi(@PathVariable String name) {
        String message = "Hello, " + name;

        // Send message to gateway.
        greetingGateway.broadcastGreet("Broadcast: " + message);
        greetingGateway.directGreet("Broadcast: " + message);

        return ok(message);
    }
}
