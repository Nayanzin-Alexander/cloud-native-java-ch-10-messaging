package com.nayanzin.integration.processfiles;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.batch.integration.launch.JobLaunchingGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageSourceSpec;
import org.springframework.integration.dsl.SourcePollingChannelAdapterSpec;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.dsl.FileInboundChannelAdapterSpec;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.io.File;
import java.util.function.Consumer;

import static org.springframework.integration.file.FileHeaders.ORIGINAL_FILE;

@Configuration
public class EtlFlowConfiguration {

    @Bean
    IntegrationFlow etlDirFlow (
            @Value("${process-files.input-directory:$HOME/Desktop/process-files}") File directory,
            ChannelsConfiguration channels,
            JobLaunchingGateway jobLaunchingGateway,
            Job job) {

        // File inbound channel adapter.
        MessageSourceSpec<FileInboundChannelAdapterSpec, FileReadingMessageSource> fileInboundChannelAdapter = Files
                .inboundAdapter(directory)
                .autoCreateDirectory(true);

        // File inbound channel adapter polling consumer.
        Consumer<SourcePollingChannelAdapterSpec> poller = consumer -> consumer.poller(p -> p.fixedRate(1000));

        // Handler
        GenericHandler<File> fileToJobLaunchRequestTransformer = (file, messageHeaders) -> {
            String absolutePath = file.getAbsolutePath();

            JobParameters params = new JobParametersBuilder()
                    .addString("file", absolutePath)
                    .toJobParameters();

            JobLaunchRequest jobLaunchRequest = new JobLaunchRequest(job, params);

            return MessageBuilder
                    .withPayload(jobLaunchRequest)
                    .setHeader(ORIGINAL_FILE, absolutePath)
                    .copyHeadersIfAbsent(messageHeaders)
                    .build();
        };

        return IntegrationFlows
                .from(fileInboundChannelAdapter, poller)
                .handle(File.class, fileToJobLaunchRequestTransformer)
                .handle(jobLaunchingGateway)
                .routeToRecipients(spec -> spec
                    .recipient(channels.invalid(), this::notFinished)
                    .recipient(channels.completed(), this::completed))
                .get();
    }

    private boolean completed(Message<?> msg) {
        Object payload = msg.getPayload();
        return ((JobExecution) payload).getExitStatus()
                .equals(ExitStatus.COMPLETED);
    }

    private boolean notFinished(Message<?> msg) {
        return !completed(msg);
    }

    @Bean
    JobLaunchingGateway jobLaunchingGateway(JobLauncher launcher) {
        return new JobLaunchingGateway(launcher);
    }
}
