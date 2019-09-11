package com.nayanzin.integration.processfiles;

import com.nayanzin.integration.service.ProcessResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageHeaders;

import java.io.File;
import java.io.IOException;

import static java.util.Optional.ofNullable;
import static org.apache.commons.io.FileUtils.moveFileToDirectory;

@Configuration
public class FinishedFileFlowsConfiguration {

    @Bean
    IntegrationFlow finishedJobsFlow(
            ChannelsConfiguration channels,
            @Value("${process-files.success.output-directory:${HOME}/Desktop/process-files/ok}") File successDir) {
        return IntegrationFlows
                .from(channels.completed())
                .handle(ProcessResult.class, moveFileHandler(successDir))
                .get();
    }

    @Bean
    IntegrationFlow failedJobsFlow(
            ChannelsConfiguration channels,
            @Value("${process-files.failed.output-directory:${HOME}/Desktop/process-files/failed}") File failedDir) {

        return IntegrationFlows
                .from(channels.invalid())
                .handle(ProcessResult.class, moveFileHandler(failedDir))
                .get();
    }

    private GenericHandler<ProcessResult> moveFileHandler(@Value("${process-files.success.output-directory:${HOME}/Desktop/process-files/ok}") File finished) {
        return (processResult, messageHeaders) -> {
            try {
                File file = extractFileFrom(messageHeaders);
                moveFileToDirectory(file, finished, true);
            } catch (IllegalStateException | ClassCastException | IOException e) {
                throw new RuntimeException("Failed to pass finished file flow due to errors", e);
            }
            return null;
        };
    }

    private File extractFileFrom(MessageHeaders messageHeaders) {
        String originalFile = ofNullable((String) messageHeaders.get(FileHeaders.ORIGINAL_FILE))
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve ORIGINAL_FILE from the message headers."));
        return new File(originalFile);
    }
}
