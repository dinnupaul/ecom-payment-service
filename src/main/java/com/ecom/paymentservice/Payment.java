package com.ecom.paymentservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "payment", schema = "payment_service")
public class Payment {

    @Id
    @Column(name = "payment_id", nullable = false, length = 50)
    private String paymentId;
    @Column(name = "customer_id", length = 50)
    private String customerId;
    @Column(name = "order_id", length = 50)
    private String orderId;
    @Column(name = "amount", length = 10)
    private Integer amount;
    @Column(name = "payment_status", length = 50)
    private String paymentStatus;
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    @Column(name = "created_at")
    private Timestamp createdAt;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        if(createdAt==null){
            createdAt= Timestamp.from(Instant.now());
        }
        this.createdAt = createdAt;
    }
}
