package org.example.ostheo_projet.service;

import org.example.ostheo_projet.Interface.Service;
import org.example.ostheo_projet.dao.ConfigurationDAO;
import org.example.ostheo_projet.event.ConfigurationUpdatedEvent;
import org.example.ostheo_projet.model.Configuration;
import org.example.ostheo_projet.model.Practitioner;
import org.example.ostheo_projet.utility.EventManager;

import java.util.List;

/**
 * Service class for managing Configuration entities.
 * Provides business logic operations for configuration-related functionality.
 */
public class ConfigurationService implements Service<Configuration> {

    private ConfigurationDAO configurationDAO;
    private EventManager eventManager = EventManager.getInstance();

    /**
     * Constructs a new ConfigurationService with the specified ConfigurationDAO.
     *
     * @param configurationDAO the data access object for configuration operations
     */
    public ConfigurationService(ConfigurationDAO configurationDAO) {
        this.configurationDAO = configurationDAO;
    }

    @Override
    public void save(Configuration configuration){
        try {
            if(configuration.getId() == 0){
                configurationDAO.insert(configuration);
                System.out.println("Configuration " + configuration.getId() + " has been saved");
            } else {
                configurationDAO.update(configuration);
                System.out.println("Configuration " + configuration.getId() + " has been updated");
            }
            eventManager.publish(new ConfigurationUpdatedEvent(configuration));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all configurations from the database.
     *
     * @return a list of all configurations, or null if an error occurs
     */
    @Override
    public List<Configuration> getAll() {
        try {
            return configurationDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a configuration by its unique identifier.
     *
     * @param id the unique identifier of the configuration
     * @return the configuration with the specified ID, or null if not found or an error occurs
     */
    @Override
    public Configuration getById(int id){
        try {
            return configurationDAO.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a configuration by a practitioner.
     *
     * @param practitioner the unique identifier of the configuration
     * @return the configuration with the specified ID, or null if not found or an error occurs
     */
    public Configuration getByPractitioner(Practitioner practitioner){
        try {
            return configurationDAO.findByPractitioner(practitioner);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Deletes the specified configuration entity from the database.
     *
     * @param entity the configuration entity to delete
     */
    @Override
    public void delete(Configuration entity) {
        try {
            configurationDAO.delete(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
