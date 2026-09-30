package org.example.ostheo_projet;

import org.example.ostheo_projet.utility.EventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class ApplicationInitializer {

    private static final Properties DATABASE_PROPERTIES = new Properties();
    public static final Logger logger = LoggerFactory.getLogger(ApplicationInitializer.class);

    private ApplicationInitializer() {}

    public static void initDatabase() {
        try {
            Path appDir = Paths.get(System.getProperty("user.home"), "Documents", "Ostheo", "Installation");
            Files.createDirectories(appDir.resolve("config"));
            Files.createDirectories(appDir.resolve("logs"));
            System.setProperty("logDir", appDir.resolve("logs").toString());

            logger.warn("test");

            // Create database config file and init database config file props by default
            Path configFile = appDir.resolve("config/database.properties");
            if (Files.notExists(configFile)) {
                logger.warn("Database properties file not found. Creating new one.");
                Properties defaults = new Properties();

                defaults.setProperty("db.url",
                        "jdbc:postgresql://localhost:5432/database_name");
                defaults.setProperty("db.username", "user");
                defaults.setProperty("db.password", "password");

                try (OutputStream out = Files.newOutputStream(configFile)) {
                    defaults.store(out, "Ostheo configuration");
                }
            }

            try (InputStream in = Files.newInputStream(configFile)) {
                DATABASE_PROPERTIES.load(in);
                logger.info("Database properties loaded.");
            } catch (IOException e){
                logger.error("Error during configuration loading : {}", e.getMessage());
                throw new RuntimeException("Une erreur est survenue lors de la lecture du fichier de configuration : ", e);
            }

        } catch (IOException e) {
            logger.error("Error during folders initialization : {}", e.getMessage());
            throw new RuntimeException("Impossible d'initialiser les dossiers de l'application : ", e);
        }
    }

    public static Properties getDatabaseProperties() {
        return DATABASE_PROPERTIES;
    }
}