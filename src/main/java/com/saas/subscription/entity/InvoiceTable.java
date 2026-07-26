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
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "InvoiceTable")
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
    @Column(name = "InvoiceID")
    private long InvoiceID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CustomerID", nullable = false)
    private CustomerTable CustomerID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "SubscriptionID", nullable = false)
    private SubscriptionsTable SubscriptionID;

    @Column(name = "InvoiceNumber", length = 50, nullable = false, unique = true)
    private String InvoiceNumber;

    @Check(constraints = "Amount >= 0")
    @Column(name = "Amount", nullable = false)
    private double Amount; //check like sql decimal(19,4)

    @Enumerated(EnumType.STRING)
    @Column(name = "InvoiceStatus", nullable = false)
    private InvoiceStatusEnum InvoiceStatus;

    @CreatedDate
    @Column(name = "InvoiceCreatedDate", nullable = false)
    private long InvoiceCreatedDate;

    @Column(name = "InvoiceUpdateDate", nullable = false)
    private long InvoiceUpdateDate;

    @Column(name = "InvoiceDueDate", nullable = false)
    private long InvoiceDueDate;

    @Column(name = "InvoicePaidDate")
    private long InvoicePaidDate;
}
