package com.example.library.common;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ErrorResponse(
        int status,
        String message,
        Instant timestamp,
        List<Map<String, String>> fieldErrors
) {}
