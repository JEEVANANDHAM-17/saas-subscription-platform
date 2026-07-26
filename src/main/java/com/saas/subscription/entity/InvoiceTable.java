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
import org.springframework.data.annotation.CreatedDate;

@Entity
public class InvoiceTable {

    public enum InvoiceStatusEnum
    {
        DRAFT,
        OPEN,
        PAID,
        VOID,
        FAILED,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long InvoiceID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerTable CustomerID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subscription_id", nullable = false)
    private SubscriptionsTable SubscriptionID;

    @Column(length = 50, nullable = false, unique = true)
    private String InvoiceNumber;

    @Check(constraints = "amount >= 0")
    @Column(nullable = false)
    private double Amount; //check like sql decimal(19,4)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatusEnum InvoiceStatus;

    @CreatedDate
    @Column(nullable = false)
    private long InvoiceCreatedDate;

    @Column(nullable = false)
    private long InvoiceUpdateDate;

    @Column(nullable = false)
    private long InvoiceDueDate;

    private long InvoicePaidDate;
}
