package org.example.ostheo_projet.utility;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.example.ostheo_projet.Interface.EntityObserver;
import org.example.ostheo_projet.model.*;

public class ApplicationContext {

    // CURRENT PATIENT CONTEXT
    private final ObjectProperty<Patient> currentPatient = new SimpleObjectProperty<>();

    public Patient getCurrentPatient() {
        return currentPatient.get();
    }

    public void setCurrentPatient(Patient patient) {
        currentPatient.set(patient);
    }

    public ObjectProperty<Patient> currentPatientProperty() {
        return currentPatient;
    }

    private final ObjectProperty<Boolean> isPatientModifying = new SimpleObjectProperty<>(false);

    public boolean isPatientModifying() {
        return isPatientModifying.get();
    }

    public void setIsPatientModifying(boolean value) {
        isPatientModifying.set(value);
    }

    public ObjectProperty<Boolean> isPatientModifyingProperty() {
        return isPatientModifying;
    }

    // PATIENT TABLE FILTERS CONTEXT
    private final ObjectProperty<PatientFilters> patientFilters = new SimpleObjectProperty<>();

    public PatientFilters getPatientFilters() {
        return patientFilters.get();
    }

    public void setPatientFilters(PatientFilters patientFilters) {
        this.patientFilters.set(patientFilters);
    }

    public ObjectProperty<PatientFilters> patientFiltersProperty() {
        return patientFilters;
    }


    // CURRENT CONSULTATION CONTEXT
    private final ObjectProperty<Consultation> currentConsultation = new SimpleObjectProperty<>();

    public Consultation getCurrentConsultation() {
        return currentConsultation.get();
    }

    public void setCurrentConsultation(Consultation currentConsultation) {
        this.currentConsultation.set(currentConsultation);
    }

    public ObjectProperty<Consultation> currentConsultationProperty() {
        return currentConsultation;
    }

    // CURRENT CONSULTATION FILTERS CONTEXT
    private final ObjectProperty<ConsultationFilters> consultationFilters = new SimpleObjectProperty<>();

    public ConsultationFilters getConsultationFilters() {
        return consultationFilters.get();
    }

    public void setConsultationFilters(ConsultationFilters consultationFilters) {
        this.consultationFilters.set(consultationFilters);
    }

    public ObjectProperty<ConsultationFilters> consultationFiltersProperty() {
        return consultationFilters;
    }


    // CURRENT PRACTITIONER CONTEXT
    private final ObjectProperty<Practitioner> currentPractitioner = new SimpleObjectProperty<>();

    public Practitioner getCurrentPractitioner() {
        return currentPractitioner.get();
    }

    public void setCurrentPractitioner(Practitioner currentPractitioner) {
        this.currentPractitioner.set(currentPractitioner);
    }

    public ObjectProperty<Practitioner> currentPractitionerProperty() {
        return currentPractitioner;
    }


}