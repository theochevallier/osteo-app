package org.example.ostheo_projet.dao;

import org.example.ostheo_projet.model.Configuration;
import org.example.ostheo_projet.model.Practitioner;
import org.example.ostheo_projet.utility.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ConfigurationDAO {

    public void insert(Configuration configuration) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(configuration);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Configuration findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Configuration.class, id);
        }
    }

    public Configuration findByPractitioner(Practitioner practitioner) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Configuration WHERE practitioner = :practitioner", Configuration.class)
                    .setParameter("practitioner", practitioner)
                    .uniqueResult();
        }
    }

    public List<Configuration> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Configuration", Configuration.class).list();
        }
    }

    public void update(Configuration configuration) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(configuration);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Configuration configuration) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(session.merge(configuration));
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
