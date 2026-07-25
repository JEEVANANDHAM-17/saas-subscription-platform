package com.saas.subscription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PaymentsTable {

    public enum PaymentMethodEnum
    {
        CARD,
        BANK,
        CASH
    }

    @Id
    private long PaymentID;

    private long InvoiceID;

    @Column(length = 100)
    private String IdempotencyKey;

    @Column(length = 100)
    private String PaymentTransactionReference;

    private double Amount;

    private PaymentMethodEnum PaymentMethod;

    @Column(length = 500)
    private String PaymentFailureReason;

    private long PaymentPaidDate;

    private long PaymentCreatedDate;

    private long PaymentUpdateDate;
}
