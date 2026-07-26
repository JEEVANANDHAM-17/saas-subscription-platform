package com.saas.subscription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Check;

@Entity
public class PaymentsTable {

    public enum PaymentMethodEnum
    {
        CARD,
        BANK,
        CASH
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long PaymentID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceTable InvoiceID;

    @Column(length = 100, nullable = false, unique = true)
    private String IdempotencyKey;

    @Column(length = 100, unique = true)
    private String PaymentTransactionReference;

    @Check(constraints = "amount > 0")
    @Column(nullable = false)
    private double Amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethodEnum PaymentMethod;

    @Column(length = 500)
    private String PaymentFailureReason;

    private long PaymentPaidDate;

    @Column(nullable = false)
    private long PaymentCreatedDate;

    @Column(nullable = false)
    private long PaymentUpdateDate;
}
