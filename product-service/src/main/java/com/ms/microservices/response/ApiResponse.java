package com.ms.microservices.response;

public record ApiResponse(
        String message,
        Object data
) {
}
