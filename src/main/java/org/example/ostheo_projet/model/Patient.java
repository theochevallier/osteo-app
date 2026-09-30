package org.example.ostheo_projet.model;

import jakarta.persistence.*;
import org.example.ostheo_projet.enums.Genre;
import org.example.ostheo_projet.enums.Osteoporose;
import org.example.ostheo_projet.utility.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Entity
@Table(name = "patient",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_patient_identity",
                        columnNames = {"lastname", "firstname", "birthday"}
                )
        })
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private String firstname;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Genre genre;

    @Column(nullable = true)
    private String address;

    private String email;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "fix_phone")
    private String fixPhone;

    @Column(name = "tutor_relative")
    private String tutorRelative;

    @Column(name = "is_english")
    private boolean isEnglish;

    @Column
    private boolean bruxisme;

    @Enumerated(EnumType.STRING)
    @Column
    private Osteoporose osteoporose;

    @Column(name = "oil_allergy")
    private boolean oilAllergy;

    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;

    @Column(name = "medical_imported_history", columnDefinition = "TEXT")
    private String medicalImportedHistory;

    @Column
    private String contraindication;

    @Column(columnDefinition = "TEXT")
    private String treatment;

    @Column(name = "canceled_appointment")
    private String canceledAppointment;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    //Hibernate needs an empty constructor
    public Patient() {
    }

    public Patient(int id, String lastname, String firstname, LocalDate birthday, Genre genre, String address, String email, String mobilePhone, String fixPhone,
                   String tutorRelative, boolean isEnglish, boolean bruxisme, Osteoporose osteoporose, boolean oilAllergy, String medicalHistory, String medicalImportedHistory,
                   String contraindication, String treatment, String canceledAppointment, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime archivedAt, Job job, City city) {
        this.id = id;
        this.lastname = lastname.toUpperCase();
        this.firstname = firstname;
        this.birthday = birthday;
        this.genre = genre;
        this.address = address;
        this.email = email;
        this.mobilePhone = mobilePhone;
        this.fixPhone = fixPhone;
        this.tutorRelative = tutorRelative;
        this.isEnglish = isEnglish;
        this.bruxisme = bruxisme;
        this.osteoporose = osteoporose;
        this.oilAllergy = oilAllergy;
        this.medicalHistory = medicalHistory;
        this.medicalImportedHistory = medicalImportedHistory;
        this.contraindication = contraindication;
        this.treatment = treatment;
        this.canceledAppointment = canceledAppointment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.archivedAt = archivedAt;
        this.job = job;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname != null ? lastname.toUpperCase() : "";
    }

    public void setLastname(String lastname) {
        this.lastname = lastname.toUpperCase();
    }

    public String getFirstname() {
        return firstname;
    }

    public String getFormattedFirstname(){
        return Utils.formatFirstname(firstname);
    }

    public String getCompleteName(){

        return this.firstname + " " + this.lastname;
    }


    public void setFirstname(String firstname) {
        this.firstname = Utils.formatFirstname(firstname);
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getAgeString() {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthday, currentDate);
        if(period.getYears() > 0){
            return period.getYears() + " ans";
        } else {
            return period.getMonths() + " mois";
        }
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getFixPhone() {
        return fixPhone;
    }

    public void setFixPhone(String fixPhone) {
        this.fixPhone = fixPhone;
    }

    public String getTutorRelative() {
        return tutorRelative;
    }

    public void setTutorRelative(String tutorRelative) {
        this.tutorRelative = tutorRelative;
    }

    public boolean isEnglish() {
        return isEnglish;
    }

    public void setEnglish(boolean english) {
        isEnglish = english;
    }

    public boolean isBruxisme() {
        return bruxisme;
    }

    public void setBruxisme(boolean bruxisme) {
        this.bruxisme = bruxisme;
    }

    public Osteoporose getOsteoporose() {
        return osteoporose;
    }

    public void setOsteoporose(Osteoporose osteoporose) {
        this.osteoporose = osteoporose;
    }

    public boolean isOilAllergy() {
        return oilAllergy;
    }

    public void setOilAllergy(boolean oilAllergy) {
        this.oilAllergy = oilAllergy;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getMedicalImportedHistory() {
        return medicalImportedHistory;
    }

    public void setMedicalImportedHistory(String medicalImportedHistory) {
        this.medicalImportedHistory = medicalImportedHistory;
    }

    public String getContraindication() {
        return contraindication;
    }

    public void setContraindication(String contraindication) {
        this.contraindication = contraindication;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getCanceledAppointment() {
        return canceledAppointment;
    }

    public void setCanceledAppointment(String canceledAppointment) {
        this.canceledAppointment = canceledAppointment;
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

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", birthday=" + birthday +
                ", genre=" + genre +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", fixPhone='" + fixPhone + '\'' +
                ", tutorRelative='" + tutorRelative + '\'' +
                ", isEnglish=" + isEnglish +
                ", bruxisme=" + bruxisme +
                ", osteoporose=" + osteoporose +
                ", oilAllergy=" + oilAllergy +
                ", medicalHistory='" + medicalHistory + '\'' +
                ", contraindication='" + contraindication + '\'' +
                ", canceledAppointment='" + canceledAppointment + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", archivedAt=" + archivedAt +
                ", job=" + job +
                ", city=" + city +
                '}';
    }

}