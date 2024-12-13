package com.ms.microservices.order.dto;

import java.math.BigDecimal;

public record OrderRequestDto(
        String skuCode,
        BigDecimal price,
        Integer quantity,
        UserDetails userDetails
) {
    public record UserDetails(String email, String firstName, String lastName) {}
}
