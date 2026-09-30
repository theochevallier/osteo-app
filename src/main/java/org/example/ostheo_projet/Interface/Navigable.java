package org.example.ostheo_projet.Interface;

import jakarta.persistence.Entity;
import org.example.ostheo_projet.enums.ActionOnOpen;

public interface Navigable {
    void onOpen(ActionOnOpen actionOnOpen);
    default void onOpen(Object entity){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    void onClose();
    void setParentConteneur(String parentConteneur);
}
