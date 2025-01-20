package com.ecom.paymentservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        //analytics_counter.increment();
        //Analytic datum =  mapper.readValue(message,Analytic.class);
        logger.info(String.format("#### -> about to consume inventory topic"));
        InventoryEvent inventoryEvent = mapper.readValue(message, InventoryEvent.class);
        logger.info(String.format("#### -> Consumed message from inventory topic in payment service-> %s", inventoryEvent.getOrderId()));

        if ("INVENTORY_CONFIRMED".equals(inventoryEvent.getInventoryStatus())) {
            // Check product availability
            paymentController.checkAndPublishPaymentMessage(inventoryEvent.getOrderRequest(), inventoryEvent.getSagaState());
        }

    }



}

