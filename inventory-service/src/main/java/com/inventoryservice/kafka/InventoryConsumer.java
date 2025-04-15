package com.inventoryservice.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.inventoryservice.service.InventoryService;

@Service
public class InventoryConsumer {

    @Autowired
    private InventoryService inventoryService;

    @KafkaListener(topics = "order-placed", groupId = "inventory-group")
    public void consumeOrderEvent(String message) {
        System.out.println("Received order event: " + message);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(message);
            JsonNode orderDetails = rootNode.path("order").path("orderDetails");

            orderDetails.forEach(orderDetail -> {
                int productId = orderDetail.get("productId").asInt();
                int quantity = orderDetail.get("quantity").asInt();
                inventoryService.updateStock(productId, quantity);
            });

        } catch (Exception e) {
            System.err.println("Error processing order event: " + e.getMessage());
        }
    }
}
