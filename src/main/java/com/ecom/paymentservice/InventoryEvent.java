package com.ecom.paymentservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryEvent {
    private String orderId;
    private String inventoryStatus;
    private OrderRequest orderRequest;
    private SagaState sagaState;
    private String traceId;
}