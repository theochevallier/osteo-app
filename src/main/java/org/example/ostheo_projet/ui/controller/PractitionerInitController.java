package org.example.ostheo_projet.ui.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.example.ostheo_projet.enums.ActionOnOpen;
import org.example.ostheo_projet.model.Configuration;
import org.example.ostheo_projet.model.Practitioner;
import org.example.ostheo_projet.service.ConfigurationService;
import org.example.ostheo_projet.service.PractitionerService;
import org.example.ostheo_projet.service.ServiceLocator;
import org.example.ostheo_projet.utility.*;

import java.util.concurrent.atomic.AtomicBoolean;

import static javafx.scene.input.KeyCode.ENTER;

public class PractitionerInitController  {
    @FXML public StackPane rootPane;
    @FXML public TextField inputUsername;
    @FXML public PasswordField inputPassword;
    @FXML public Label lblMessage;
    @FXML public Label lblTitle;
    public TextField inputFirstname;
    public TextField inputLastname;
    public PasswordField inputConfirmedPassword;
    public Button btnSave;
    public VBox formPane;

    private PractitionerService practitionerService;
    private ConfigurationService configurationService;
    private NavigationManager navigationManager;
    private ApplicationContext applicationContext;

    public PractitionerInitController(){
    }

    public void initService(PractitionerService practitionerService, NavigationManager navigationManager){
        this.practitionerService = practitionerService;
        this.navigationManager = navigationManager;
        this.applicationContext = ServiceLocator.INSTANCE.getApplicationContext();
        this.configurationService = ServiceLocator.INSTANCE.getConfigurationService();
    }

    public void initialize(){
        // Set an event handler when enter key pressed to fire connexion button
        rootPane.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == ENTER) {
                btnSave.fire();
                keyEvent.consume();
            }
        });

        Form.initInputFormatters(inputLastname, inputFirstname);
    }

    @FXML
    public void handleSaveInit(ActionEvent actionEvent) {
        lblMessage.setVisible(false);

        if(createNewPractitioner()){
            showRootWindow(actionEvent);
        }

    }

    public boolean createNewPractitioner(){
        String username = inputUsername.getText();
        String firstname = inputFirstname.getText();
        String lastname = inputLastname.getText();
        String password = inputPassword.getText();
        String confirmedPassword = inputConfirmedPassword.getText();
        lblMessage.setVisible(false);

        // Verify that both password are equals
        if(!password.equals(confirmedPassword)){
            lblMessage.setText("Les mots de passes ne sont pas identiques.");
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setVisible(true);
            return false;
        }

        AtomicBoolean valid = new AtomicBoolean(true);
        Form.forEachNodeOfType(formPane, TextField.class, textField -> {
            if(textField.getText().isEmpty()) valid.set(false);
        });

        if(!valid.get()) return false;

        //the regex in replaceAll removes all whitespaces and invisible chars in Strings
        if(username.replaceAll("\\s+","").isEmpty() || password.replaceAll("\\s+","").isEmpty()){
            lblMessage.setText("Veuillez remplir tous les champs.");
            lblMessage.setVisible(true);
            return false;
        }

        // Hash the password to stock it
        String hashedPassword = Encryption.hashString(password);
        Practitioner practitioner = new Practitioner(0, firstname, lastname, username, hashedPassword);
        System.out.println("Creating new practitioner: " + practitioner);
        this.practitionerService.save(practitioner);

        Platform.runLater(() -> {
            lblMessage.setText("Compte créé avec succès.");
            lblMessage.setStyle("-fx-text-fill: green;");
            lblMessage.setVisible(true);
        });

        // Set the current user credential in the application context
        this.applicationContext.setCurrentPractitioner(practitioner);

        createNewConfiguration();

        return true;
    }

    public void createNewConfiguration(){
        Configuration configuration = new Configuration();
        configuration.setPractitioner(this.applicationContext.getCurrentPractitioner());
        this.configurationService.save(configuration);
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
}
