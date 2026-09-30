package org.example.ostheo_projet.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "configuration")
public class Configuration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "cabinet_name")
    private String cabinetName;

    @Column(name = "cabinet_address")
    private String cabinetAddress;

    @Column(name = "cabinet_city")
    private String cabinetCity;

    @Column(name = "cabinet_postal_code")
    private String cabinetPostalCode;

    @Column(name = "cabinet_country")
    private String cabinetCountry;

    @Column(name = "invoice_consult_label")
    private String invoiceConsultationLabel;

    @Column(name = "invoice_end_text")
    private String invoiceEndText;

    @Column(name = "defaultConsultFilePath")
    private String defaultConsultFilePath;

    @Column(name = "maxResultsFindAll")
    private int maxResultsFindAll;

    @Column(name = "last_used_cheque_deposit")
    private String lastUsedChequeDeposit;

    @ManyToOne
    @JoinColumn(name = "practitioner_id")
    private Practitioner practitioner;

    public Configuration() {
        this.maxResultsFindAll = 500;
    }

    public Configuration(int id, String cabinetName, String cabinetAddress, String cabinetCity, String cabinetPostalCode,
                         String cabinetCountry, String invoiceConsultationLabel, String invoiceEndText,
                         String defaultConsultFilePath, int maxResultsFindAll, Practitioner practitioner, String lastUsedChequeDeposit) {
        this.id = id;
        this.cabinetName = cabinetName;
        this.cabinetAddress = cabinetAddress;
        this.cabinetCity = cabinetCity;
        this.cabinetPostalCode = cabinetPostalCode;
        this.cabinetCountry = cabinetCountry;
        this.invoiceConsultationLabel = invoiceConsultationLabel;
        this.invoiceEndText = invoiceEndText;
        this.defaultConsultFilePath = defaultConsultFilePath;
        this.maxResultsFindAll = maxResultsFindAll;
        this.practitioner = practitioner;
        this.lastUsedChequeDeposit = lastUsedChequeDeposit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCabinetName() {
        return cabinetName;
    }

    public void setCabinetName(String cabinetName) {
        this.cabinetName = cabinetName;
    }

    public String getCabinetAddress() {
        return cabinetAddress;
    }

    public void setCabinetAddress(String cabinetAddress) {
        this.cabinetAddress = cabinetAddress;
    }

    public String getCabinetCity() {
        return cabinetCity;
    }

    public void setCabinetCity(String cabinetCity) {
        this.cabinetCity = cabinetCity;
    }

    public String getCabinetPostalCode() {
        return cabinetPostalCode;
    }

    public void setCabinetPostalCode(String cabinetPostalCode) {
        this.cabinetPostalCode = cabinetPostalCode;
    }

    public String getCabinetCountry() {
        return cabinetCountry;
    }

    public void setCabinetCountry(String cabinetCountry) {
        this.cabinetCountry = cabinetCountry;
    }

    public String getInvoiceConsultationLabel() {
        return invoiceConsultationLabel;
    }

    public void setInvoiceConsultationLabel(String invoiceConsultationLabel) {
        this.invoiceConsultationLabel = invoiceConsultationLabel;
    }

    public String getInvoiceEndText() {
        return invoiceEndText;
    }

    public void setInvoiceEndText(String invoiceEndText) {
        this.invoiceEndText = invoiceEndText;
    }

    public String getDefaultConsultFilePath() {
        return defaultConsultFilePath;
    }

    public void setDefaultConsultFilePath(String defaultConsultFilePath) {
        this.defaultConsultFilePath = defaultConsultFilePath;
    }

    public int getMaxResultsFindAll() {
        return maxResultsFindAll;
    }

    public void setMaxResultsFindAll(int maxResultsFindAll) {
        this.maxResultsFindAll = maxResultsFindAll;
    }

    public String getLastUsedChequeDeposit() {
        return lastUsedChequeDeposit;
    }

    public void setLastUsedChequeDeposit(String lastUsedChequeDeposit) {
        this.lastUsedChequeDeposit = lastUsedChequeDeposit;
    }

    public Practitioner getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(Practitioner practitioner) {
        this.practitioner = practitioner;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "id=" + id +
                ", cabinetName='" + cabinetName + '\'' +
                ", cabinetAddress='" + cabinetAddress + '\'' +
                ", cabinetCity='" + cabinetCity + '\'' +
                ", cabinetPostalCode='" + cabinetPostalCode + '\'' +
                ", cabinetCountry='" + cabinetCountry + '\'' +
                ", invoiceConsultationLabel='" + invoiceConsultationLabel + '\'' +
                ", invoiceEndText='" + invoiceEndText + '\'' +
                ", defaultConsultFilePath='" + defaultConsultFilePath + '\'' +
                ", practitioner=" + practitioner +
                '}';
    }
}