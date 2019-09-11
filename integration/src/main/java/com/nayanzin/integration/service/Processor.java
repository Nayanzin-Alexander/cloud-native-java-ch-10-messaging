package com.nayanzin.integration.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class Processor {

    private static Random r = new Random();

    public ProcessResult process(ProcessRequest o) {
        return new ProcessResult(r.nextBoolean());
    }
}
