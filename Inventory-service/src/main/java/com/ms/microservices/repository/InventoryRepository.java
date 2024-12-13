package com.ms.microservices.repository;

import com.ms.microservices.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository  extends JpaRepository<Inventory, Long> {

    boolean  existsByProductNameAndQuantityIsGreaterThanEqual(String productName, Integer quantity);
}
