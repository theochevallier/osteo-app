package org.example.ostheo_projet.service;

import org.example.ostheo_projet.Interface.Service;
import org.example.ostheo_projet.dao.ConsultationDAO;
import org.example.ostheo_projet.model.Consultation;
import org.example.ostheo_projet.model.Patient;

import java.time.LocalDate;
import java.util.List;

public class ConsultationService implements Service<Consultation> {
    private final ConsultationDAO consultationDAO;

    public ConsultationService(ConsultationDAO consultationDAO) {
        this.consultationDAO = consultationDAO;
    }

    @Override
    public Consultation getById(int id) {
        try {
            return consultationDAO.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Consultation> getByPatient(Patient patient) {
        try {
            return consultationDAO.findByPatient(patient);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getCountByPatient(Patient patient){
        try {
            return consultationDAO.findCountByPatient(patient);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<Consultation> getByFullTextSearch(int limitResult, String textSearch, LocalDate date){
        try {
            return consultationDAO.findByFullTextSearchAndDate(limitResult, textSearch, date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(Consultation entity) {
        try {
            consultationDAO.delete(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Consultation entity) {
        try {
            if (entity.getId() == 0) {
                consultationDAO.insert(entity);
            } else {
                consultationDAO.update(entity);
            }
            System.out.println("Consultation saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Consultation> getAll() {
        try {
            return consultationDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Consultation> getAll(int limit) {
        try {
            return consultationDAO.findAll(limit);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Consultation> getAllGroupByPatientConsultDate() {
        try {
            return consultationDAO.findAllGroupByPatientConsultDate();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
