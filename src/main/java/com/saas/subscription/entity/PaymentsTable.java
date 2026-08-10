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
import jakarta.persistence.Table;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "PaymentsTable")
public class PaymentsTable {

    public enum PaymentMethodEnum
    {
        CARD,
        BANK,
        CASH
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PaymentID")
    private long paymentID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "InvoiceID", nullable = false)
    private InvoiceTable invoiceID;

    @Column(name = "IdempotencyKey", length = 100, nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "PaymentTransactionReference", length = 100, unique = true)
    private String paymentTransactionReference;

    @Check(constraints = "Amount > 0")
    @Column(name = "Amount", nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "PaymentMethod", nullable = false)
    private PaymentMethodEnum paymentMethod;

    @Column(name = "PaymentFailureReason", length = 500)
    private String paymentFailureReason;

    @Column(name = "PaymentPaidDate")
    private long paymentPaidDate;

    @Column(name = "PaymentCreatedDate", nullable = false)
    private long paymentCreatedDate;

    @Column(name = "PaymentUpdateDate", nullable = false)
    private long paymentUpdateDate;
}
