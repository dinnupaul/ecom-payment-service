package com.ecom.paymentservice;

import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentView toPaymentView(Payment payment) {
        PaymentView paymentView = new PaymentView();
        paymentView.setPaymentId(payment.getPaymentId());
        paymentView.setCustomerId(payment.getCustomerId());
        paymentView.setOrderId(payment.getOrderId());
        paymentView.setAmount(payment.getAmount());
        paymentView.setPaymentStatus(payment.getPaymentStatus());
        paymentView.setPaymentMethod(payment.getPaymentMethod());
        paymentView.setCreatedAt(payment.getCreatedAt());
        return paymentView;
    }
}
