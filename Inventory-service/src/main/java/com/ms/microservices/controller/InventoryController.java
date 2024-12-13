package com.ms.microservices.controller;

import com.ms.microservices.dto.ApiResponse;
import com.ms.microservices.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> isInStock(@RequestParam("ProductName") String productName, @RequestParam("Quantity") Integer quantity) {
        try{
            return ResponseEntity.ok().body(new ApiResponse("Busca Realizada", inventoryService.isInStock(productName, quantity)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

}
