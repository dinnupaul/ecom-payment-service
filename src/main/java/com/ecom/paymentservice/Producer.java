package com.ecom.paymentservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Random;

@Service
public class Producer
{
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    private static final String TOPIC = "product-topic";

    @Autowired //DEPENDENCY INJECTION PROMISE FULFILLED AT RUNTIME
    private KafkaTemplate<String, String> kafkaTemplate ;

    @Autowired
    ObjectMapper objectMapper;

    public void pubUpdateProductDetailsMessage(String principal,
                                            String description) throws JsonProcessingException // LOGIN | REGISTER
    {
        Analytic analytic = new Analytic();
        analytic.setObjectid(String.valueOf((new Random()).nextInt()));
        analytic.setType("UPDATE");
        analytic.setPrincipal(principal);
        analytic.setDescription(description);
        analytic.setTimestamp(LocalTime.now()); // SETTING THE TIMESTAMP OF THE MESSAGE

        // convert to JSON
        String datum =  objectMapper.writeValueAsString(analytic);

        logger.info(String.format("#### -> Producing message -> %s", datum));
        this.kafkaTemplate.send(TOPIC,datum);
    }


    public void publishPaymentStatusMessage(OrderRequest request, String paymentStatus, SagaState sagaState) throws JsonProcessingException {
        // Publish payment response
        PaymentEvent paymentEvent = new PaymentEvent(request.getOrderId(), paymentStatus, request, sagaState);
        String paymentEventJson = objectMapper.writeValueAsString(paymentEvent);
        kafkaTemplate.send("payment-topic", request.getOrderId(), paymentEventJson);
    }


    public void publishOrderRetryMessage(OrderRequest request, String paymentStatus) throws JsonProcessingException {
        // Publish inventory response
        InterimEvent interimEvent = new InterimEvent(request.getOrderId(), "Payment", paymentStatus);
        String interimEventJson = objectMapper.writeValueAsString(interimEvent);
        kafkaTemplate.send("order-retry-topic", request.getOrderId(), interimEventJson);
    }

}
