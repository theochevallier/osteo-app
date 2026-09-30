package org.example.ostheo_projet.ui.controller;

import jakarta.persistence.Entity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.example.ostheo_projet.Interface.Navigable;
import org.example.ostheo_projet.enums.ActionOnOpen;
import org.example.ostheo_projet.model.Practitioner;
import org.example.ostheo_projet.service.PractitionerService;
import org.example.ostheo_projet.service.ServiceLocator;
import org.example.ostheo_projet.utility.*;

import static javafx.scene.input.KeyCode.ENTER;

public class ConnexionController implements Navigable {
    @FXML public StackPane rootPane;
    @FXML public TextField inputUsername;
    @FXML public Button btnConnexion;
    @FXML public PasswordField inputPassword;
    @FXML public Label lblMessage;
    @FXML public Label lblTitle;

    private PractitionerService practitionerService;
    private NavigationManager navigationManager;
    private ApplicationContext applicationContext;

    public ConnexionController(){
    }

    public void initService(PractitionerService practitionerService, NavigationManager navigationManager){
        this.practitionerService = practitionerService;
        this.navigationManager = navigationManager;
        this.applicationContext = ServiceLocator.INSTANCE.getApplicationContext();

        Practitioner userPractitioner = this.practitionerService.getByLastUpdate();
        inputUsername.setText(userPractitioner.getUsername());
        inputPassword.requestFocus();
    }

    // Move any code that needs credentialService to method initService
    public void initialize(){
        // Set an event handler when enter key pressed to fire connexion button
        rootPane.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == ENTER) {
                btnConnexion.fire();
                keyEvent.consume();
            }
        });
    }

    @FXML
    public void handleConnexion(ActionEvent actionEvent) {
        lblMessage.setVisible(false);

        if(verifyCredentials()){
            // Close window
//            rootPane.getScene().getWindow().hide();

            showRootWindow(actionEvent);
        }

    }

    public boolean verifyCredentials(){
        String username = inputUsername.getText();
        String password = inputPassword.getText();

        //the regex in replaceAll removes all whitespaces and invisible chars in Strings
        if(username.replaceAll("\\s+","").isEmpty() || password.replaceAll("\\s+","").isEmpty()){
            lblMessage.setText("Veuillez remplir tous les champs.");
            lblMessage.setVisible(true);
            return false;
        }

        Practitioner userPractitioner = this.practitionerService.getByUsername(username);
        if(userPractitioner == null){
            lblMessage.setText("Identifiant ou mot de passe incorrect.");
            lblMessage.setVisible(true);
            return false;
        }

        boolean result = Encryption.verifyPassword(password, userPractitioner.getPassword());
        if(!result){
            lblMessage.setText("Identifiant ou mot de passe incorrect.");
            lblMessage.setVisible(true);
            return false;
        }

        // Set the current user credential in the application context
        this.applicationContext.setCurrentPractitioner(userPractitioner);

        return true;
    }

    public void showRootWindow(ActionEvent actionEvent) {
        Stage stage = Window.createOrShowWindow("main-window", this, "Fenêtre principale", "main-window.fxml", true, 1000, 800, true);
        stage.setMaximized(true);
        stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
        this.navigationManager.setStage(stage);

        // Hide current window
        rootPane.getScene().getWindow().hide();

        // Set the main window controller to the logger
        Scene scene = stage.getScene();
        MainWindowController controller = (MainWindowController) scene.getRoot().getUserData();
        Logger.getInstance().setMainWindowController(controller);
    }

    @Override
    public void onOpen(ActionOnOpen actionOnOpen) {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void setParentConteneur(String rootConteneur) {
    }
}
