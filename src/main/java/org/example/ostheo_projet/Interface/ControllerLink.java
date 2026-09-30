package org.example.ostheo_projet.Interface;


// Interface qui permet de lier un controlleur parent avec son enfant (pour pouvoir mettre à jour des données sur une autre fenêtre)
public interface ControllerLink {
    public void setParentController(Object controller);


}
