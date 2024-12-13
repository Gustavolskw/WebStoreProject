package com.ms.microservices.order.controller;

import com.ms.microservices.order.dto.ApiResponse;
import com.ms.microservices.order.dto.OrderRequestDto;
import com.ms.microservices.order.dto.OrderResponseDto;
import com.ms.microservices.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("api/order")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> createOrder(@RequestBody OrderRequestDto orderRequestDto, UriComponentsBuilder uriBuilder) {
        try{
            OrderResponseDto orderResponseDto = orderService.placeOrder(orderRequestDto);
            URI url = uriBuilder.path("api/order/{id}").buildAndExpand(orderResponseDto.id()).toUri();
        return ResponseEntity.created(url).body(new ApiResponse("Order placed with success", orderResponseDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }
}
