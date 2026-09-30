package org.example.ostheo_projet.service;

import org.example.ostheo_projet.Interface.Service;
import org.example.ostheo_projet.dao.JobDAO;
import org.example.ostheo_projet.model.Job;

import java.util.List;

/**
 * Service class for managing Job entities.
 * Provides business logic operations for job-related functionality.
 */
public class JobService implements Service<Job> {

    private final JobDAO jobDAO;

    /**
     * Constructs a new JobService with the specified JobDAO.
     *
     * @param jobDAO the data access object for job operations
     */
    public JobService(JobDAO jobDAO) {
        this.jobDAO = jobDAO;
    }

    /**
     * Retrieves all jobs from the database.
     *
     * @return a list of all jobs, or null if an error occurs
     */
    @Override
    public List<Job> getAll(){
        try {
            return jobDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a job by its name.
     * The name is automatically formatted with proper capitalization (first letter uppercase, rest lowercase).
     *
     * @param name the name of the job to search for
     * @return the job with the specified name, or null if not found or an error occurs
     */
    @Override
    public Job getByName(String name) {
        try {
            return jobDAO.findByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves a job to the database.
     * If the job has an ID (not 0), it updates the existing job.
     * If the job has no ID (0), it inserts a new job.
     * Throws an exception if a job with the same name already exists.
     *
     * @param job the job entity to save
     * @throws Exception if a job with the same name already exists
     */
    @Override
    public void save(Job job) throws Exception {
        try {
            // Verification si job existe
            if(jobDAO.findByName(job.getName()) != null){
                throw new Exception("Job already exists");
            }

            if(job.getId() != 0){
                jobDAO.update(job);
            } else {
                jobDAO.insert(job);
            }
            System.out.println("Job " + job.getId() + " has been saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a job by its unique identifier.
     *
     * @param id the unique identifier of the job
     * @return the job with the specified ID, or null if not found or an error occurs
     */
    @Override
    public Job getById(int id) {
        try {
            return jobDAO.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Deletes the specified job entity from the database.
     *
     * @param job the job entity to delete
     */
    @Override
    public void delete(Job job) {
        try {
            jobDAO.delete(job);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
