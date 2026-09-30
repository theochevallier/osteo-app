package org.example.ostheo_projet.utility;

import org.example.ostheo_projet.enums.MessageType;
import org.example.ostheo_projet.service.ServiceLocator;
import org.example.ostheo_projet.ui.controller.MainWindowController;

public class Logger {

    private static Logger instance;
    private MainWindowController mainWindowController;

    private Logger() {
    }

    public static Logger getInstance() {
        if(instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    public void logFile(String message){

    }

    public void showMessagePopUp(String message, MessageType messageType){
        this.mainWindowController.showMessagePopUp(message, messageType);
    }
}
