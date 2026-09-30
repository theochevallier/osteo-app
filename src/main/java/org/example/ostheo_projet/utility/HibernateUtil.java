package org.example.ostheo_projet.utility;

import org.example.ostheo_projet.ApplicationInitializer;
import org.example.ostheo_projet.service.PatientService;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;

import java.util.Properties;

public class HibernateUtil {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(HibernateUtil.class);

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure();
            Properties properties = ApplicationInitializer.getDatabaseProperties();
            System.out.println("URL : " + properties.getProperty("db.url"));
            System.out.println("Username : " + properties.getProperty("db.username"));
            System.out.println("Password : " + properties.getProperty("db.password"));

            configuration.setProperty("hibernate.connection.url", properties.getProperty("db.url"));
            configuration.setProperty("hibernate.connection.username", properties.getProperty("db.username"));
            configuration.setProperty("hibernate.connection.password", properties.getProperty("db.password"));

            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("SessionFactory creation failed: " + ex);
            logger.error("Echec de la connexion à la base de donnée :{}", ex.getMessage());
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}