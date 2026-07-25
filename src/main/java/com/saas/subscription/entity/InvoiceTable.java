package com.saas.subscription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    private long InvoiceID;

    private long CustomerID;

    private long SubscriptionID;

    @Column(length = 50)
    private String InvoiceNumber;

    private double Amount; //check like sql decimal(19,4)

    private InvoiceStatusEnum InvoiceStatus;

    @CreatedDate
    private long InvoiceCreatedDate;

    private long InvoiceUpdateDate;

    private long InvoiceDueDate;

    private long InvoicePaidDate;
}
