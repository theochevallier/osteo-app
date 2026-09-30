package org.example.ostheo_projet.dao;

import jakarta.persistence.criteria.*;
import org.example.ostheo_projet.model.*;
import org.example.ostheo_projet.utility.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    public void insert(Invoice invoice) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(invoice);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Invoice findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Invoice.class, id);
        }
    }

    public List<Invoice> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Invoice ORDER BY consultation.consultationDate desc", Invoice.class).list();
        }
    }

    public List<Invoice> findAll(int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Invoice ORDER BY consultation.consultationDate desc", Invoice.class).setMaxResults(limit).list();
        }
    }

    public List<Invoice> findByPredicates(int month, int year, PaymentType paymentType, String chequeDepositId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Invoice> query = cb.createQuery(Invoice.class);
            Root<Invoice> root = query.from(Invoice.class);
            Join<Invoice, Consultation> consultationJoin = root.join("consultation", JoinType.INNER);
            List<Predicate> predicates = new ArrayList<>();

            Path<LocalDate> date = consultationJoin.get("consultationDate");

            if (month > 0 && month <= 12) {
                predicates.add(cb.equal(cb.function("date_part", Double.class, cb.literal("month"), date), month));
            }

            if (year > 0) {
                predicates.add(cb.equal(cb.function("date_part", Double.class, cb.literal("year"), date), year));
            }

            if (paymentType != null) {
                predicates.add(cb.equal(root.get("paymentType"), paymentType));
            }

            if(chequeDepositId != null && !chequeDepositId.isEmpty()) {
                predicates.add(cb.equal(root.get("chequeDepositId"), chequeDepositId));
            }

            // Combiner tous les prédicats avec AND
            if (!predicates.isEmpty()) {
                query.where(cb.and(predicates.toArray(new Predicate[0])));
            }

            return session.createQuery(query).list();
        }
    }

    public void update(Invoice invoice) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(invoice);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Invoice invoice) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(session.merge(invoice));
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public List<ExportCompta> findAllExportData(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT new org.example.ostheo_projet.model.ExportCompta(" +
                                    "p.lastname, p.firstname, p.email, c.name, c.postalCode, " +
                                    "i.id, i.amount, pt.name) " +
                                    "FROM Invoice i " +
                                    "JOIN Consultation co on i.consultation.id = co.id " +
                                    "JOIN Patient p on co.patient.id = p.id " +
                                    "JOIN City c on c.id = p.city.id " +
                                    "JOIN PaymentType pt on pt.id = i.paymentType.id ",
                            ExportCompta.class)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Invoice> findByConsultation(Consultation consultation) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Invoice where consultation.id = :consultationId", Invoice.class)
                    .setParameter("consultationId", consultation.getId()).list();
        }
    }
}
