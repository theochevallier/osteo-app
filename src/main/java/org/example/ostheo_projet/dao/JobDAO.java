package org.example.ostheo_projet.dao;

import org.example.ostheo_projet.model.Job;
import org.example.ostheo_projet.utility.HibernateUtil;
import org.example.ostheo_projet.utility.Utils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class JobDAO {

    public void insert(Job job) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Format job name
            String formattedName = Utils.formatStringFirstLetterUppercase(job.getName());
            job.setName(formattedName);
            session.persist(job);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Job findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Job.class, id);
        }
    }

    public Job findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Job WHERE LOWER(name) = :name", Job.class)
                          .setParameter("name", name).setParameter("name", name.toLowerCase())
                          .uniqueResult();
        }
    }

    public List<Job> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Job", Job.class).list();
        }
    }

    public void update(Job job) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(job);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Job job) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(session.merge(job));
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}