package org.example.ostheo_projet.service;

import org.example.ostheo_projet.Interface.Service;
import org.example.ostheo_projet.dao.CityDAO;
import org.example.ostheo_projet.dao.JobDAO;
import org.example.ostheo_projet.dao.PatientDAO;
import org.example.ostheo_projet.enums.Genre;
import org.example.ostheo_projet.event.PatientUpdatedEvent;
import org.example.ostheo_projet.model.City;
import org.example.ostheo_projet.model.Invoice;
import org.example.ostheo_projet.model.Job;
import org.example.ostheo_projet.model.Patient;
import org.example.ostheo_projet.utility.EventManager;
import org.example.ostheo_projet.utility.Utils;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatientService implements Service<Patient> {

    private final PatientDAO patientDAO;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PatientService.class);
    private EventManager eventManager = EventManager.getInstance();

    public PatientService(PatientDAO patientDAO, JobDAO jobDAO) {
        this.patientDAO = patientDAO;
    }

    @Override
    public List<Patient> getAll(){
        try {
            List<Patient> patients = patientDAO.findAll();
            return patients == null ? new ArrayList<>() : patients;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Patient> getAll(int limit){
        try {
            List<Patient> patients = patientDAO.findAll(limit);
            return patients == null ? new ArrayList<>() : patients;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param firstname the string param must be format : "firstname NAME"
     */
    public List<Patient> getByName(String lastname, String firstname) {
        try {
            if(lastname == null || firstname == null){
                return null;
            }

            // Format the firstname to have uppercase in first letter
            String formattedFirstname = Utils.formatFirstname(firstname);
            System.out.println(formattedFirstname);
            return patientDAO.findByName(lastname.toUpperCase(), formattedFirstname);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Patient> getByCriteria(int limitResult, String name, String firstname, LocalDate birthday, String city, String postalCode, String mobilePhone, String fixPhone, String email, Genre genre){
        try {
            List<Patient> patients = patientDAO.findByCriteria(limitResult, name, firstname, birthday, city, postalCode, mobilePhone, fixPhone, email, genre);
            if(patients == null){
                return new ArrayList<>();
            }
            return patients;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(Patient patient) throws Exception {
        try {
            // Verification si nouveau user mais qu'il existe déjà en base, throw exception
            List<Patient> existingPatients = patientDAO.findByCriteria(1, patient.getLastname(), patient.getFormattedFirstname(), patient.getBirthday(), null, null, null, null, null, null);
            if((existingPatients != null && !existingPatients.isEmpty()) && patient.getId() == 0){
                logger.error("User already exists");
                throw new Exception("User already exists");
            }

            // Formatage du firstname
            patient.setFirstname(patient.getFirstname());

            if(patient.getId() != 0){
                patientDAO.update(patient);
            } else {
                patientDAO.insert(patient);
            }
            eventManager.publish(new PatientUpdatedEvent(patient));
            logger.info("User {} has been saved", patient.getId());
            System.out.println("User " + patient.getId() + " has been saved");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Patient getById(int id) {
        try {
            return patientDAO.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(Patient patient) {
        try {
            patientDAO.delete(patient);
            eventManager.publish(new PatientUpdatedEvent(patient));
            logger.info("User {} has been deleted", patient.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
