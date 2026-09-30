package org.example.ostheo_projet.service;

import org.example.ostheo_projet.Interface.Service;
import org.example.ostheo_projet.dao.PaymentTypeDAO;
import org.example.ostheo_projet.model.PaymentType;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentTypeService implements Service<PaymentType> {
    private final PaymentTypeDAO paymentTypeDAO;

    public PaymentTypeService(PaymentTypeDAO paymentTypeDAO) {
        this.paymentTypeDAO = paymentTypeDAO;
    }

    @Override
    public List<PaymentType> getAll() {
        try {
            return paymentTypeDAO.findAll(false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<PaymentType> getAll(boolean withArchived) {
        try {
            return paymentTypeDAO.findAll(withArchived);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(PaymentType entity) throws Exception {
        try {
            if(getByName(entity.getName()) != null && entity.getId() == 0){
                throw new Exception("PaymentType with name " + entity.getName() + " already exists");
            }

            if(entity.getId() != 0){
                paymentTypeDAO.update(entity);
            } else {
                paymentTypeDAO.insert(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return;
    }

    @Override
    public PaymentType getById(int id) {
        try {
            return paymentTypeDAO.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public PaymentType getByName(String name) {
        try {
            return paymentTypeDAO.findByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isPaymentTypeUsed(int id){
        try {
            return paymentTypeDAO.isPaymentTypeUsed(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void delete(PaymentType entity) {
        try {
            paymentTypeDAO.delete(entity);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void archive(PaymentType entity){
        try {
            if(entity.getArchivedAt() == null){
                entity.setArchivedAt(LocalDateTime.now());
            } else {
                entity.setArchivedAt(null);
            }
            paymentTypeDAO.update(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
