package org.example.ostheo_projet.abstracts;

import org.example.ostheo_projet.Interface.Service;

public abstract class Factory {
    public abstract Service createService(Object dao);

}
