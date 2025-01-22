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
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/v1/payments")
public class PaymentController
{
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class.getName());
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentMapper paymentMapper;

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

    public void publishPaymentMessage(String orderId) {
    }


    public void checkAndPublishPaymentMessage(OrderRequest orderRequest,SagaState  sagaState) throws JsonProcessingException {
        boolean paymentSuccess = false;
        if ("INVENTORY_FAILED".equals( sagaState.getCurrentState())){
            paymentSuccess = false;
        }else {
            paymentSuccess = processPayment(orderRequest, true);
        }
        String paymentStatus = paymentSuccess ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED";

        // Publish inventory status event
        // logger.info("Publish payment status event");
        sagaState.updateStepStatus("Payment", paymentStatus);
        sagaState.setCurrentState(paymentStatus);
        redisTemplate.opsForValue().set("ORDER_" + orderRequest.getOrderId(), sagaState);
       // if(paymentSuccess) {
            producer.publishPaymentStatusMessage(orderRequest, paymentStatus, sagaState);
      //  }
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

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllPayments() {
        try {
            List<Payment> payments = paymentRepository.findAll();

            if (payments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No payments available.");
            }

            return ResponseEntity.ok(payments.stream()
                    .map(paymentMapper::toPaymentView)
                    .collect(Collectors.toList()));

        } catch (Exception e) {
            // Log the error (logging can be added here for production)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching payments: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<PaymentView> fetchAllPayments() {
        try {
            List<Payment> payments = paymentRepository.findAll();

            if (payments.isEmpty()) {
                return new ArrayList<>();
            }

            return payments.stream()
                    .map(paymentMapper::toPaymentView)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // Log the error (logging can be added here for production)
            return new ArrayList<>();
        }
    }


}
