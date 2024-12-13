package com.ms.microservices.order.service;

import com.ms.microservices.order.client.InventoryClient;
import com.ms.microservices.order.dto.OrderRequestDto;
import com.ms.microservices.order.dto.OrderResponseDto;
import com.ms.microservices.order.event.OrderPlacedEvent;
import com.ms.microservices.order.model.Order;
import com.ms.microservices.order.respository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    public OrderService(OrderRepository orderRepository, InventoryClient inventoryClient, KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderResponseDto placeOrder(OrderRequestDto orderDto) {
        Boolean isProdcutInStock  = inventoryClient.isInStock(orderDto.skuCode(), orderDto.quantity()).data();
        if(!isProdcutInStock) throw new RuntimeException("Item is not in stock");
        Order order  = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setPrice(orderDto.price());
        order.setSkuCode(orderDto.skuCode());
        order.setQuantity(orderDto.quantity());
        orderRepository.save(order);
        OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(order.getOrderNumber(), orderDto.userDetails().email());
//        orderPlacedEvent.setOrderNumber(order.getOrderNumber());
//        orderPlacedEvent.setEmail(orderDto.userDetails().email());
//        orderPlacedEvent.setFirstName(orderDto.userDetails().firstName());
//        orderPlacedEvent.setLastName(orderDto.userDetails().lastName());
        log.info("OrderPlacedEvent: {}", orderPlacedEvent);
        kafkaTemplate.send("order-placed", orderPlacedEvent);

        return new OrderResponseDto(order);

    }
}
