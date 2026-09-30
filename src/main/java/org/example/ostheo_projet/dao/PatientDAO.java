package org.example.ostheo_projet.dao;

import jakarta.persistence.criteria.*;
import org.example.ostheo_projet.enums.Genre;
import org.example.ostheo_projet.model.City;
import org.example.ostheo_projet.model.Patient;
import org.example.ostheo_projet.utility.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public void insert(Patient patient) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            patient.setCreatedAt(LocalDateTime.now());
            session.persist(patient);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Patient findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Patient.class, id);
        }
    }

    public List<Patient> findByName(String lastname, String firstname) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Patient WHERE lastname = :lastname AND firstname = :firstname", Patient.class)
                          .setParameter("lastname", lastname).setParameter("firstname", firstname).list();
        }
    }

    public List<Patient> findByCriteria(int limitResult, String name, String firstname, LocalDate birthday, String city, String postalCode, String mobileTel, String fixTel, String email, Genre genre) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Patient> query = cb.createQuery(Patient.class);
            Root<Patient> root = query.from(Patient.class);
            Join<Patient, City> cityJoin = root.join("city", JoinType.LEFT);
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.upper(root.get("lastname")), "%" + name.toUpperCase() + "%"));
            }
            if (firstname != null && !firstname.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("firstname")), "%" + firstname.toLowerCase() + "%"));
            }
            if(birthday != null) {
                predicates.add(cb.equal(root.get("birthday"), birthday));
            }
            if(email != null && !email.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }

            // City and postal code
            if (city != null && !city.isEmpty()) {
                predicates.add(cb.like(cb.lower(cityJoin.get("name")), "%" + city.toLowerCase() + "%"));
            }
            if (postalCode != null && !postalCode.isEmpty()) {
                predicates.add(cb.like(cb.lower(cityJoin.get("postalCode")), "%" + postalCode.toLowerCase() + "%"));
            }

            if (mobileTel != null && !mobileTel.isEmpty()) {
                predicates.add(cb.like(root.get("mobilePhone"), "%" + mobileTel.toLowerCase() + "%"));
            }
            if (fixTel != null && !fixTel.isEmpty()) {
                predicates.add(cb.like(root.get("fixPhone"), "%" + fixTel.toLowerCase() + "%"));
            }
            if (genre != null && genre != Genre.AUCUN) {
                predicates.add(cb.equal(root.get("genre"), genre));
            }

            // Combiner tous les prédicats avec AND
            if (!predicates.isEmpty()) {
                query.where(cb.and(predicates.toArray(new Predicate[0])));
            }

            return session.createQuery(query).setMaxResults(limitResult).list();
        }
    }

    public List<Patient> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Patient WHERE archivedAt IS NULL ORDER BY COALESCE(updatedAt, createdAt) DESC ", Patient.class).list();
        }
    }

    public List<Patient> findAll(int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Patient WHERE archivedAt IS NULL ORDER BY COALESCE(updatedAt, createdAt) DESC ", Patient.class).setMaxResults(limit).list();
        }
    }

    public void update(Patient patient) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            patient.setUpdatedAt(LocalDateTime.now());
            session.merge(patient);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Patient patient) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(session.merge(patient));
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}