package com.nayanzin.integration.processfiles;

import com.nayanzin.integration.service.ProcessRequest;
import com.nayanzin.integration.service.ProcessResult;
import com.nayanzin.integration.service.Processor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.MessageSelector;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageSourceSpec;
import org.springframework.integration.dsl.SourcePollingChannelAdapterSpec;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.dsl.FileInboundChannelAdapterSpec;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.support.MessageBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.springframework.integration.file.FileHeaders.ORIGINAL_FILE;

@Configuration
public class EtlFlowConfiguration {

    private MessageSelector finishedSelector = message -> ((ProcessResult) message.getPayload()).isResult();
    private MessageSelector notFinishedSelector = message -> !finishedSelector.accept(message);

    @Bean
    IntegrationFlow etlDirFlow(
            @Value("${process-files.input-directory:${HOME}/Desktop/process-files}") File directory,
            ChannelsConfiguration channels,
            Processor processor) {

        // File inbound channel adapter.
        MessageSourceSpec<FileInboundChannelAdapterSpec, FileReadingMessageSource> fileInboundChannelAdapter = Files
                .inboundAdapter(directory)
                .autoCreateDirectory(true);

        // File inbound channel adapter polling consumer.
        Consumer<SourcePollingChannelAdapterSpec> poller = consumer -> consumer.poller(p -> p.fixedRate(1000));

        // Handler: fileToProcessRequestTransformer
        GenericHandler<File> fileToProcessRequestTransformer = (file, messageHeaders) -> {
            String absolutePath = file.getAbsolutePath();
            Map<String, Object> requestParams = new HashMap<>(messageHeaders);
            requestParams.put("file", absolutePath);
            ProcessRequest jobLaunchRequest = new ProcessRequest(requestParams);
            return MessageBuilder
                    .withPayload(jobLaunchRequest)
                    .setHeader(ORIGINAL_FILE, absolutePath)
                    .copyHeadersIfAbsent(messageHeaders)
                    .build();
        };

        // Handler: process request, return ProcessResult.
        GenericHandler<ProcessRequest> launchRequest =
                (jobLaunchRequest, messageHeaders) -> MessageBuilder
                        .withPayload(processor.process(jobLaunchRequest))
                        .copyHeadersIfAbsent(messageHeaders)
                        .build();

        return IntegrationFlows
                .from(fileInboundChannelAdapter, poller)
                .filter(File.class, file -> !file.isDirectory())
                .handle(File.class, fileToProcessRequestTransformer)
                .handle(ProcessRequest.class, launchRequest)
                .routeToRecipients(spec -> spec
                        .recipient(channels.invalid(), notFinishedSelector)
                        .recipient(channels.completed(), finishedSelector))
                .get();
    }
}
