package com.ecom.paymentservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/payments")
public class PaymentController
{
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class.getName());
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    Producer producer = new Producer();

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
}
