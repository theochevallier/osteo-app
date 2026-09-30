package org.example.ostheo_projet.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExportCompta {
//    Patient
    private String patientLastname;
    private String patientFirstname;

    // Consultation
    private String consultationDate;

//    Facture
    private String invoiceNumber;
    private double amount;
    private String paymentType;

    public ExportCompta(String patientLastname, String patientFirstname , String invoiceNumber, LocalDate consultationDate, double amount, String paymentType) {
        this.patientLastname = patientLastname;
        this.patientFirstname = patientFirstname;
        this.invoiceNumber = invoiceNumber;
        this.consultationDate = consultationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.amount = amount;
        this.paymentType = paymentType;
    }

    public String getPatientLastname() {
        return patientLastname;
    }

    public void setPatientLastname(String patientLastname) {
        this.patientLastname = patientLastname;
    }

    public String getPatientFirstname() {
        return patientFirstname;
    }

    public void setPatientFirstname(String patientFirstname) {
        this.patientFirstname = patientFirstname;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getConsultationDate() {
        return consultationDate;
    }

    public void setConsultationDate(LocalDate consultationDate) {
        this.consultationDate = consultationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public String toString() {
        return "ExportCompta{" +
                "patientName='" + patientLastname + '\'' +
                ", patientFirstname='" + patientFirstname + '\'' +
                ", amount=" + amount +
                ", paymentType='" + paymentType + '\'' +
                '}';
    }
}
