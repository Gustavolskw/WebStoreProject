package com.ms.microservices.service;

import com.ms.microservices.event.ProductCreatedEvent;
import com.ms.microservices.model.Inventory;
import com.ms.microservices.repository.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public boolean isInStock(String productName, Integer quantity) {
        return  inventoryRepository.existsByProductNameAndQuantityIsGreaterThanEqual(productName, quantity);
    }


    @KafkaListener(topics = "Product-created")
    public void listen(ProductCreatedEvent productCreatedEvent) {
        log.info("Received Product Creation {}", productCreatedEvent);
        Inventory inventory = new Inventory();
        inventory.setProductName(productCreatedEvent.getProductName());
        inventory.setQuantity(0);
        inventoryRepository.save(inventory);
    }

}
