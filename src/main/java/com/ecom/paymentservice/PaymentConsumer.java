package com.ecom.paymentservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PaymentConsumer
{
    private final Logger logger = LoggerFactory.getLogger(PaymentConsumer.class);


    @Autowired
    ObjectMapper mapper;
    @Autowired
    PaymentController paymentController;

    @KafkaListener(topics = "inventory-topic", groupId = "ecom-payment-service")
    public void consumeInventoryEvents(String message) throws IOException
    {
        logger.info(String.format("#### -> about to consume inventory topic"));
        InventoryEvent inventoryEvent = mapper.readValue(message, InventoryEvent.class);
        logger.info(String.format("#### -> Consumed message from inventory topic in payment service-> %s", inventoryEvent.getOrderId()));
            // Check product availability
        // Add trace ID to MDC
        String traceId = inventoryEvent.getTraceId();
        if (traceId != null) {
            MDC.put("traceId", traceId);
        }
            paymentController.checkAndPublishPaymentMessage(inventoryEvent.getOrderRequest(), inventoryEvent.getSagaState());
    }

}

