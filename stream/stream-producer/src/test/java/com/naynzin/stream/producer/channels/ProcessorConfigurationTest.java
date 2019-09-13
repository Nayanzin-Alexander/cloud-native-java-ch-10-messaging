package com.naynzin.stream.producer.channels;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ProcessorConfigurationTest {

    @Test
    public void ProcessorConfigurationTest() {
        assertThat(new ProcessorConfiguration().transform("foo"), is("FOO"));
    }

    // todo https://spring.io/blog/2017/10/24/how-to-test-spring-cloud-stream-applications-part-i
}