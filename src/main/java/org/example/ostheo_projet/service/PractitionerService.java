package org.example.ostheo_projet.service;

import org.example.ostheo_projet.Interface.EntityObserver;
import org.example.ostheo_projet.Interface.Service;
import org.example.ostheo_projet.dao.PractitionerDAO;
import org.example.ostheo_projet.event.PractionerUpdatedEvent;
import org.example.ostheo_projet.model.Practitioner;
import org.example.ostheo_projet.utility.ApplicationContext;
import org.example.ostheo_projet.utility.EventManager;


public class PractitionerService implements Service<Practitioner> {
    private final PractitionerDAO practitionerDAO;
    private EventManager eventManager = EventManager.getInstance();

    public PractitionerService(PractitionerDAO practitionerDAO) {
        this.practitionerDAO = practitionerDAO;
    }



    @Override
    public void save(Practitioner practitioner)  {
        try {
            if(practitioner.getId() != 0){
                practitionerDAO.update(practitioner);

                eventManager.publish(new PractionerUpdatedEvent(practitioner));

                System.out.println("Practitioner " + practitioner.getId() + " has been updated");
            } else {
                practitionerDAO.insert(practitioner);
                System.out.println("Practitioner " + practitioner.getId() + " has been created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Practitioner getById(int id) {
        try {
            return practitionerDAO.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Practitioner getByUsername(String username) {
        try {
            return practitionerDAO.findByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Practitioner getByLastUpdate(){
        try {
            return practitionerDAO.findByLastUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(Practitioner practitioner) {
        try {
            practitionerDAO.delete(practitioner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
