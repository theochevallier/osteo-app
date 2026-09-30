package org.example.ostheo_projet.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultation")
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "consultation_date")
    private LocalDate consultationDate;

    @Column
    private String reason;

    @Column
    private String treatment;

    @Column(name = "medical_exam")
    private String medicalExam;

    @Column(name = "exclusion_test")
    private String exclusionTest;

    @Column
    private String note;

    @Column(name = "given_advice")
    private String givenAdvice;

    @Column(name = "given_exercise")
    private String givenExercise;

    @Column(name = "food_supplement")
    private String foodSupplement;

    @Column(name = "is_doctor_relocation")
    private boolean isDoctorRelocation = false;

    @Column(name = "is_dentiste_relocation")
    private boolean isDentistRelocation = false;

    @Column(name = "is_orthophoniste_relocation")
    private boolean isOrthophonisteRelocation = false;

    @Column(name = "is_orthodontiste_relocation")
    private boolean isOrthodontisteRelocation = false;

    @Column(name = "is_orthoptiste_relocation")
    private boolean isOrthoptisteRelocation = false;

    @Column(name = "is_podologue_relocation")
    private boolean isPodologueRelocation = false;

    @Column(name = "is_ibclc_relocation")
    private boolean isIbclcRelocation = false;

    @Column(name = "relocation_note")
    private String relocationNote;

    // Transient field allows to store a value in the java object but not in the database.
    @Transient
    private int consultationNumber;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "practitioner_id", nullable = false)
    private Practitioner practitioner;

    public Consultation(){}

    public Consultation(Patient patient, Practitioner practitioner) {
        this.patient = patient;
        this.practitioner = practitioner;
    }

    public Consultation(int id, LocalDate consultationDate, String reason, String treatment, String medicalExam, String exclusionTest, String note,
                        String givenAdvice, String givenExercise, String foodSupplement, boolean isDoctorRelocation, boolean isDentistRelocation,
                        boolean isOrthophonisteRelocation, boolean isOrthodontisteRelocation, boolean isOrthoptisteRelocation, boolean isPodologueRelocation,
                        boolean isIbclcRelocation, String relocationNote, LocalDateTime createdAt, LocalDateTime updatedAt, Patient patient, Practitioner practitioner) {
        this.id = id;
        this.consultationDate = consultationDate;
        this.reason = reason;
        this.treatment = treatment;
        this.medicalExam = medicalExam;
        this.exclusionTest = exclusionTest;
        this.note = note;
        this.givenAdvice = givenAdvice;
        this.givenExercise = givenExercise;
        this.foodSupplement = foodSupplement;
        this.isDoctorRelocation = isDoctorRelocation;
        this.isDentistRelocation = isDentistRelocation;
        this.isOrthophonisteRelocation = isOrthophonisteRelocation;
        this.isOrthodontisteRelocation = isOrthodontisteRelocation;
        this.isOrthoptisteRelocation = isOrthoptisteRelocation;
        this.isPodologueRelocation = isPodologueRelocation;
        this.isIbclcRelocation = isIbclcRelocation;
        this.relocationNote = relocationNote;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.patient = patient;
        this.practitioner = practitioner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getConsultationDate() {
        return consultationDate;
    }

    public void setConsultationDate(LocalDate consultationDate) {
        this.consultationDate = consultationDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getMedicalExam() {
        return medicalExam;
    }

    public void setMedicalExam(String medicalExam) {
        this.medicalExam = medicalExam;
    }

    public String getExclusionTest() {
        return exclusionTest;
    }

    public void setExclusionTest(String exclusionTest) {
        this.exclusionTest = exclusionTest;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getGivenAdvice() {
        return givenAdvice;
    }

    public void setGivenAdvice(String givenAdvice) {
        this.givenAdvice = givenAdvice;
    }

    public String getGivenExercise() {
        return givenExercise;
    }

    public void setGivenExercise(String givenExercise) {
        this.givenExercise = givenExercise;
    }

    public String getFoodSupplement() {
        return foodSupplement;
    }

    public void setFoodSupplement(String foodSupplement) {
        this.foodSupplement = foodSupplement;
    }

    public boolean isDoctorRelocation() {
        return isDoctorRelocation;
    }

    public void setDoctorRelocation(boolean doctorRelocation) {
        isDoctorRelocation = doctorRelocation;
    }

    public boolean isDentistRelocation() {
        return isDentistRelocation;
    }

    public void setDentistRelocation(boolean dentistRelocation) {
        isDentistRelocation = dentistRelocation;
    }

    public boolean isOrthophonisteRelocation() {
        return isOrthophonisteRelocation;
    }

    public void setOrthophonisteRelocation(boolean orthophonisteRelocation) {
        isOrthophonisteRelocation = orthophonisteRelocation;
    }

    public boolean isOrthodontisteRelocation() {
        return isOrthodontisteRelocation;
    }

    public void setOrthodontisteRelocation(boolean orthodontisteRelocation) {
        isOrthodontisteRelocation = orthodontisteRelocation;
    }

    public boolean isOrthoptisteRelocation() {
        return isOrthoptisteRelocation;
    }

    public void setOrthoptisteRelocation(boolean orthoptisteRelocation) {
        isOrthoptisteRelocation = orthoptisteRelocation;
    }

    public boolean isPodologueRelocation() {
        return isPodologueRelocation;
    }

    public void setPodologueRelocation(boolean podologueRelocation) {
        isPodologueRelocation = podologueRelocation;
    }

    public boolean isIbclcRelocation() {
        return isIbclcRelocation;
    }

    public void setIbclcRelocation(boolean ibclcRelocation) {
        isIbclcRelocation = ibclcRelocation;
    }

    public String getRelocationNote() {
        return relocationNote;
    }

    public void setRelocationNote(String relocationNote) {
        this.relocationNote = relocationNote;
    }

    public int getConsultationNumber() {
        return consultationNumber;
    }

    public void setConsultationNumber(int consultationNumber) {
        this.consultationNumber = consultationNumber;
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Practitioner getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(Practitioner practitioner) {
        this.practitioner = practitioner;
    }

    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + id +
                ", consultationDate=" + consultationDate +
                ", reason='" + reason + '\'' +
                ", treatment='" + treatment + '\'' +
                ", medicalExam='" + medicalExam + '\'' +
                ", exclusionTest='" + exclusionTest + '\'' +
                ", note='" + note + '\'' +
                ", givenAdvice='" + givenAdvice + '\'' +
                ", givenExercise='" + givenExercise + '\'' +
                ", foodSupplement='" + foodSupplement + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", patient=" + patient +
                ", practitioner=" + practitioner +
                '}';
    }
}
