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
    private long invoiceID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CustomerID", nullable = false)
    private CustomerTable customerID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "SubscriptionID", nullable = false)
    private SubscriptionsTable subscriptionID;

    @Column(name = "InvoiceNumber", length = 50, nullable = false, unique = true)
    private String invoiceNumber;

    @Check(constraints = "Amount >= 0")
    @Column(name = "Amount", nullable = false)
    private double amount; //check like sql decimal(19,4)

    @Enumerated(EnumType.STRING)
    @Column(name = "InvoiceStatus", nullable = false)
    private InvoiceStatusEnum invoiceStatus;

    @CreatedDate
    @Column(name = "InvoiceCreatedDate", nullable = false)
    private long invoiceCreatedDate;

    @Column(name = "InvoiceUpdateDate", nullable = false)
    private long invoiceUpdateDate;

    @Column(name = "InvoiceDueDate", nullable = false)
    private long invoiceDueDate;

    @Column(name = "InvoicePaidDate")
    private long invoicePaidDate;
}
