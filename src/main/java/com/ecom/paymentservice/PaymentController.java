package com.ecom.paymentservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;


@RestController
@RequestMapping("api/v1/payments")
public class PaymentController
{
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class.getName());
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    Producer producer = new Producer();
    private final RedisTemplate<String, Object> redisTemplate;

    public PaymentController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/update") // URIs SERVE CHUNKS OF DATA UNLIKE URLs WHICH SERVE PAGES
    public ResponseEntity<String> updateProductDetails(@RequestBody Payment payment) throws JsonProcessingException {
        logger.info("initiating payment update in Paymentontroller");
        paymentRepository.save(payment);
        logger.info(" payment update completed successfully in payment Table");
        logger.info(payment.getPaymentStatus()," initiating payment topic");
        producer.pubUpdateProductDetailsMessage(payment.getPaymentStatus(), "PAYMENT DETAILS UPDATED SUCCESSFULLY");

        return ResponseEntity.ok("Details Updated Successfully");
    }

    //@GetMapping("get/restros")
    //public ResponseEntity<?> getRestros() throws InterruptedException
    //{
    //    Thread.sleep(1000);
     //   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    //}

    @GetMapping("/getAll")
    public ResponseEntity<Payment> getProduct(@PathVariable String id) {
        Payment payment = paymentRepository.getReferenceById(id);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

                //new ResponseEntity(product.get(0));
                //product.map(ResponseEntity::ok)
               // .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public void publishPaymentMessage(String orderId) {
    }

    /**** public void publishInventoryMessage(OrderRequest request) throws JsonProcessingException {
        boolean isAvailable = checkProductAvailability(request.getProductId());
        String status = isAvailable ? "INVENTORY_CONFIRMED" : "INVENTORY_FAILED";

        // Publish order creation event
        logger.info("Publish Inventory status event");
        producer.publishInventoryMessage(request,  status);
    }

    public boolean checkProductAvailability(String productId) {
        Optional<Inventory> inventory = inventoryRepository.findById(productId);
        return inventory.isPresent(); // Simulate available product
    } ***/


    public void checkAndPublishPaymentMessage(OrderRequest orderRequest,SagaState  sagaState) throws JsonProcessingException {
        boolean paymentSuccess = processPayment(orderRequest, true);
        String paymentStatus = paymentSuccess ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED";

        // Publish inventory status event
        // logger.info("Publish payment status event");
        sagaState.updateStepStatus("Payment", paymentStatus);
        sagaState.setCurrentState(paymentStatus);
        redisTemplate.opsForValue().set("ORDER_" + orderRequest.getOrderId(), sagaState);
        if(paymentSuccess) {
            producer.publishPaymentStatusMessage(orderRequest, paymentStatus, sagaState);
        }
       // producer.publishOrderRetryMessage(orderRequest, paymentStatus);

    }

    private boolean processPayment(OrderRequest orderRequest, boolean isPaymentSuccessful) throws JsonProcessingException {
        // Mock payment processing
        Payment payment = new Payment(UUID.randomUUID().toString() ,orderRequest.getCustomerId(), orderRequest.getOrderId(),
                1000 , isPaymentSuccessful ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED","Cash");
        paymentRepository.save(payment);
        return true; // Simulate successful payment
    }

    public void rollbackPayment(String orderId) {
      /**  Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(orderId));

        // Reverse payment (mock logic)
        payment.setStatus("ROLLBACK");
        paymentRepository.save(payment); **/
    }

}
