package com.nayanzin.integration.service;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ProcessRequest {
    private Map<String, Object> requestParam;
}
