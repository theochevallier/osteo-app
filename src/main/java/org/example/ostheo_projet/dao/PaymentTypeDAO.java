package org.example.ostheo_projet.dao;

import org.example.ostheo_projet.model.PaymentType;
import org.example.ostheo_projet.model.PaymentType;
import org.example.ostheo_projet.utility.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class PaymentTypeDAO {

    public void insert(PaymentType paymentType) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(paymentType);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public List<PaymentType> findAll(boolean withArchived) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            String query = "FROM PaymentType" + (withArchived ? "" : " WHERE archivedAt IS NULL");
            return session.createQuery(query, PaymentType.class).list();
        }
    }

    public PaymentType findById(int id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(PaymentType.class, id);
        }
    }

    public PaymentType findByName(String name) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM PaymentType WHERE LOWER(name) = :name", PaymentType.class)
                    .setParameter("name", name.toLowerCase())
                    .uniqueResult();
        }
    }

    public boolean isPaymentTypeUsed(int id){
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return !session.createQuery("FROM Invoice WHERE paymentType.id = :id", PaymentType.class)
                    .setParameter("id", id)
                    .list().isEmpty();
        }
    }

    public void update(PaymentType paymentType) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(paymentType);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(PaymentType paymentType) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(session.merge(paymentType));
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
