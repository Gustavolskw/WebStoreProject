package com.ms.microservices.dto;

import com.ms.microservices.model.Product;

import java.math.BigDecimal;

public record ProductResponseDto(String id, String name, String description, BigDecimal price) {
    public ProductResponseDto(Product product) {
        this(product.getId(), product.getName(), product.getDescription(), product.getPrice());
    }
}
