package com.ms.microservices.service;


import com.ms.microservices.dto.ProductRequestDto;
import com.ms.microservices.dto.ProductResponseDto;
import com.ms.microservices.event.ProductCreatedEvent;
import com.ms.microservices.model.Product;
import com.ms.microservices.respository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    public ProductService(ProductRepository productRepository, KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public ProductResponseDto storeProduct(ProductRequestDto productRequestDto) {
        Product product = Product.builder()
                .name(productRequestDto.name())
                .description(productRequestDto.description())
                .price(productRequestDto.price())
                .build();
        productRepository.save(product);
        log.info("Product store successful");
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(product.getId(), product.getName(), product.getPrice());
        log.info("ProductCreatedEvent: {}", productCreatedEvent);
        kafkaTemplate.send("Product-created", productCreatedEvent);
        return new ProductResponseDto(product);
    }

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream().map(ProductResponseDto::new).toList();
    }
}
