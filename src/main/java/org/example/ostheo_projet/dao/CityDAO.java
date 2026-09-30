package org.example.ostheo_projet.dao;

import org.example.ostheo_projet.model.City;
import org.example.ostheo_projet.utility.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class CityDAO {

    public void insert(City city) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(city);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public City findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(City.class, id);
        }
    }

    public City findByName(String name){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM City WHERE name = :name", City.class)
                          .setParameter("name", name)
                          .uniqueResult();
        }
    }

    public List<City> findByPostalCode(String postalCode) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM City WHERE postalCode = :postalCode", City.class)
                    .setParameter("postalCode", postalCode).list();
        }
    }

    public List<City> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM City", City.class).list();
        }
    }

    public void update(City city) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(city);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(City city) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(session.merge(city));
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public City findByNamePostalCode(String name, String postalCode) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM City WHERE name = :name AND postalCode = :postalCode", City.class)
                    .setParameter("name", name).setParameter("postalCode", postalCode)
                    .uniqueResult();
        }
    }
}
