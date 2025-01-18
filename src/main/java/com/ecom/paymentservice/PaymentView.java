package com.ecom.paymentservice;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter @Setter
public class PaymentView
{
    String paymentId;
    String customerId;
    String orderId;
    Integer amount;
    String paymentStatus;
    String paymentMethod;
    Timestamp createdAt;
}
