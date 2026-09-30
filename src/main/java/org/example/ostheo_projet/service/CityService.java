package org.example.ostheo_projet.service;

import org.example.ostheo_projet.Interface.Service;
import org.example.ostheo_projet.dao.CityDAO;
import org.example.ostheo_projet.model.City;
import org.slf4j.Logger;

import java.util.List;

/**
 * Service class for managing City entities.
 * Provides business logic operations for city-related functionality.
 */
public class CityService implements Service<City> {

    private CityDAO cityDAO;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(CityService.class);

    /**
     * Constructs a new CityService with the specified CityDAO.
     *
     * @param cityDAO the data access object for city operations
     */
    public CityService(CityDAO cityDAO) {
        this.cityDAO = cityDAO;
    }

    @Override
    public void save(City entity) throws Exception {
        try {
            City existingCity = cityDAO.findByNamePostalCode(entity.getName(), entity.getPostalCode());
            if (existingCity != null) {
                logger.error("City with name '{}' and postal code '{}' already exists.", entity.getName(), entity.getPostalCode());
                throw new Exception("City with name '" + entity.getName() + "' and postal code '" + entity.getPostalCode() + "' already exists.");
            }

            if(entity.getId() == 0){
                cityDAO.insert(entity);
            } else {
                cityDAO.update(entity);
            }
            logger.info("City {} has been saved", entity.getId());
            System.out.println("City " + entity.getId() + " has been saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all cities from the database.
     *
     * @return a list of all cities, or null if an error occurs
     */
    @Override
    public List<City> getAll() {
        try {
            return cityDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a city by its name.
     *
     * @param name the name of the city to search for
     * @return the city with the specified name, or null if not found or an error occurs
     */
    @Override
    public City getByName(String name){
        try {
            return cityDAO.findByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a city by its unique identifier.
     *
     * @param id the unique identifier of the city
     * @return the city with the specified ID, or null if not found or an error occurs
     */
    @Override
    public City getById(int id){
        try {
            return cityDAO.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves all cities with the specified postal code.
     *
     * @param postalCode the postal code to search for
     * @return a list of cities with the specified postal code, or null if an error occurs
     */
    public List<City> getByPostalCode(String postalCode){
        try {
            return cityDAO.findByPostalCode(postalCode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a city by its name and postal code combination.
     *
     * @param name the name of the city
     * @param postalCode the postal code of the city
     * @return the city with the specified name and postal code, or null if not found or an error occurs
     */
    public City getByNamePostalCode(String name, String postalCode){
        try {
            return cityDAO.findByNamePostalCode(name, postalCode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Deletes the specified city entity from the database.
     *
     * @param entity the city entity to delete
     */
    @Override
    public void delete(City entity) {
        try {
            cityDAO.delete(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
