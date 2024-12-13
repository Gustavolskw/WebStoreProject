package com.ms.microservices.controller;

import com.ms.microservices.dto.ProductRequestDto;
import com.ms.microservices.response.ApiResponse;
import com.ms.microservices.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/product")
public class ProductController {

    private final ProductService productService;


    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> createProduct(@RequestBody ProductRequestDto productRequestDto) {
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Product created sucessfully", productService.storeProduct(productRequestDto)));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> getAllProducts() {
        try{
        return ResponseEntity.ok().body(new ApiResponse("List of All products", productService.getAllProducts()));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }
}
