package com.ms.microservices.order.client;

public record ClientResponse<T>(String message, T data) {
}
