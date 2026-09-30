package org.example.ostheo_projet.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Transient
    private String invoiceNumber;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "cheque_ref")
    private String chequeRef;

    @Column(name = "cheque_deposit_id")
    private String chequeDepositId;

    @ManyToOne
    @JoinColumn(name = "payment_type_id", nullable = false)
    private PaymentType paymentType;

    @OneToOne
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    public Invoice(){}

    public Invoice(int id, Double amount, String chequeRef, String chequeDepositId,
                   PaymentType paymentType, Consultation consultation) {
        this.id = id;
        this.amount = amount;
        this.chequeRef = chequeRef;
        this.chequeDepositId = chequeDepositId;
        this.paymentType = paymentType;
        this.consultation = consultation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return String.format("FA%07d", id);
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getChequeRef() {
        return chequeRef;
    }

    public void setChequeRef(String chequeRef) {
        this.chequeRef = chequeRef;
    }

    public String getChequeDepositId() {
        return chequeDepositId;
    }

    public void setChequeDepositId(String chequeDepositId) {
        this.chequeDepositId = chequeDepositId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", amount=" + amount +
                ", chequeRef='" + chequeRef + '\'' +
                ", chequeDepositId='" + chequeDepositId + '\'' +
                ", paymentType=" + paymentType +
                ", consultation=" + consultation +
                '}';
    }
}
