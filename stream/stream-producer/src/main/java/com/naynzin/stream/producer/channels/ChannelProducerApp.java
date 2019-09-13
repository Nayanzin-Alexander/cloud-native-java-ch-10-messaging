package com.naynzin.stream.producer.channels;

import com.naynzin.stream.producer.ProducerChannels;
import com.naynzin.stream.producer.SinkChannels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;

import static org.springframework.http.ResponseEntity.ok;

@SpringBootApplication
@EnableBinding({
        ProducerChannels.class,
        SinkChannels.class
})
public class ChannelProducerApp {

    @Autowired
    ProducerChannels producerChannels;
    @Autowired
    SinkChannels sink;

    public static void main(String[] args) {
        SpringApplication.run(ChannelProducerApp.class);
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return (args -> IntStream.range(0, 10).forEach(i -> {
            producerChannels.broadcastChannel().send(MessageBuilder.withPayload("Broadcasting: Hi, " + i).build());
            producerChannels.directChannel().send(MessageBuilder.withPayload("Sending Direct: Hi, " + i).build());
        }));
    }

    @Bean
    IntegrationFlow broadcastListener() {
        return IntegrationFlows
                .from(sink.broadcastGreetingSink())
                .handle(String.class, (payload, headers) -> {
                    System.out.println("Received: " + payload);
                    return null;
                })
                .get();
    }

    @RestController
    class Controller {

        private final ProducerChannels producerChannels;

        public Controller(@Autowired ProducerChannels producerChannels) {
            this.producerChannels = producerChannels;
        }

        @GetMapping("/hi/{name}")
        public ResponseEntity greet(@PathVariable String name) {
            producerChannels.broadcastChannel().send(MessageBuilder.withPayload("Broadcasting: Hi, " + name).build());
            producerChannels.directChannel().send(MessageBuilder.withPayload("Sending Direct: Hi, " + name).build());
            return ok(name);
        }
    }
}



