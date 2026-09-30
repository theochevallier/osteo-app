package org.example.ostheo_projet.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "practitioner")
public class Practitioner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String firstname;

    @Column
    private String lastname;

    @Column
    private String email;

    @Column
    private String phone;

    @Column(name = "rpps_id")
    private String rppsId;

    @Column(name = "username",  unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "invoice_practitioner_label")
    private String invoicePractitionerLabel;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    public Practitioner() {}

    public Practitioner(int id, String firstname, String lastname, String username, String password){
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
    }

    public Practitioner(int id, String firstname, String lastname, String email, String phone, String rppsId, String username,
                        String password, String invoicePractitionerLabel, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime archivedAt) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.rppsId = rppsId;
        this.username = username;
        this.password = password;
        this.invoicePractitionerLabel = invoicePractitionerLabel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.archivedAt = archivedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRppsId() {
        return rppsId;
    }

    public void setRppsId(String rppsId) {
        this.rppsId = rppsId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInvoicePractitionerLabel() {
        return invoicePractitionerLabel;
    }

    public void setInvoicePractitionerLabel(String invoicePractitionerLabel) {
        this.invoicePractitionerLabel = invoicePractitionerLabel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    @Override
    public String toString() {
        return "Practitioner{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", rppsId='" + rppsId + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", invoicePractitionerLabel='" + invoicePractitionerLabel + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", archivedAt=" + archivedAt +
                '}';
    }
}