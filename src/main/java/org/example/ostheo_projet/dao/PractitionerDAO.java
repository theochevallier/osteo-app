package org.example.ostheo_projet.dao;

import org.example.ostheo_projet.model.Practitioner;
import org.example.ostheo_projet.utility.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PractitionerDAO {

    public void insert(Practitioner practitioner) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            practitioner.setCreatedAt(LocalDateTime.now());
            session.persist(practitioner);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public Practitioner findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Practitioner.class, id);
        }
    }

    public Practitioner findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Practitioner where username = :username", Practitioner.class).setParameter("username", username).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Practitioner findByLastUpdate(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Practitioner ORDER BY COALESCE(updatedAt, createdAt) DESC", Practitioner.class).setMaxResults(1).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void update(Practitioner practitioner) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            practitioner.setUpdatedAt(LocalDateTime.now());
            session.merge(practitioner);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Practitioner practitioner) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(session.merge(practitioner));
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
