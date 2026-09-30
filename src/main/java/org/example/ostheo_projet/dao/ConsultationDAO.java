package org.example.ostheo_projet.dao;

import jakarta.persistence.criteria.*;
import org.example.ostheo_projet.enums.Genre;
import org.example.ostheo_projet.model.City;
import org.example.ostheo_projet.model.Consultation;
import org.example.ostheo_projet.model.Patient;
import org.example.ostheo_projet.utility.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsultationDAO {

    public void insert(Consultation consultation) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            consultation.setCreatedAt(LocalDateTime.now());
            session.persist(consultation);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Consultation findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Consultation.class, id);
        }
    }

    public List<Consultation> findByPatient(Patient patient) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Consultation> consultations = session.createQuery("FROM Consultation WHERE patient = :patient ORDER BY consultationDate", Consultation.class)
                    .setParameter("patient", patient)
                    .list();
            addConsultationNumberToConsultationList(consultations);
            return consultations;
        }
    }

    public List<Consultation> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Consultation> consultations = session.createQuery("FROM Consultation c ORDER BY consultationDate", Consultation.class).list();

            addConsultationNumberToConsultationList(consultations);
            return consultations;
        }
    }

    public List<Consultation> findAll(int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Consultation> consultations = session.createQuery("FROM Consultation c ORDER BY consultationDate", Consultation.class).setMaxResults(limit).list();

            addConsultationNumberToConsultationList(consultations);
            return consultations;
        }
    }

    public int findCountByPatient(Patient patient) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return (int) session.createQuery("FROM Consultation WHERE patient = :patient", Consultation.class)
                    .setParameter("patient", patient)
                    .stream().count();
        }
    }


    public List<Consultation> findByFullTextSearchAndDate(int limitResult, String textSearch, LocalDate date) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM consultation c
                WHERE 1=1
            """);

            if (textSearch != null && !textSearch.isBlank()) {
                sql.append("""
                    AND to_tsvector(
                        coalesce(reason,'') || ' ' ||
                        coalesce(treatment,'') || ' ' ||
                        coalesce(medical_exam,'') || ' ' ||
                        coalesce(exclusion_test,'') || ' ' ||
                        coalesce(given_advice,'') || ' ' ||
                        coalesce(given_exercise,'') || ' ' ||
                        coalesce(note,'')
                    ) @@ plainto_tsquery(:textSearch)
                """);
            }

            if (date != null) {
                sql.append("""
                    AND DATE(c.consultation_date) = :date
                """);
            }

            NativeQuery<Consultation> query = session.createNativeQuery(sql.toString(), Consultation.class);

            if (textSearch != null && !textSearch.isBlank()) {
                query.setParameter("textSearch", textSearch);
            }

            if (date != null) {
                query.setParameter("date", date);
            }

            return query.setMaxResults(limitResult).getResultList();
        }
    }

    public List<Consultation> findAllGroupByPatientConsultDate(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Consultation ORDER BY patient.id, consultationDate", Consultation.class).list();
        }
    }

    public void update(Consultation consultation) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            consultation.setUpdatedAt(LocalDateTime.now());
            session.merge(consultation);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Consultation consultation) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(session.merge(consultation));
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void addConsultationNumberToConsultationList(List<Consultation> consultations) {
        Map<Integer, Integer> counters = new HashMap<>();

        for (Consultation c : consultations) {
            int number = counters.merge(
                    c.getPatient().getId(),
                    1,
                    Integer::sum
            );

            c.setConsultationNumber(number);
        }
    }
}
