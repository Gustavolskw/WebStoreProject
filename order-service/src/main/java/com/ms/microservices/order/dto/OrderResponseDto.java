package com.ms.microservices.order.dto;

import com.ms.microservices.order.model.Order;

import java.math.BigDecimal;

public record OrderResponseDto(Long id,
                               String orderNumber,
                               String skuCode,
                               BigDecimal price,
                               Integer quantity) {
    public OrderResponseDto(Order order){
        this(order.getId(), order.getOrderNumber(), order.getSkuCode(), order.getPrice(), order.getQuantity());
    }
}
