package org.example.ostheo_projet.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.ostheo_projet.model.Practitioner;
import org.example.ostheo_projet.service.PractitionerService;
import org.example.ostheo_projet.service.ServiceLocator;
import org.example.ostheo_projet.utility.ApplicationContext;
import org.example.ostheo_projet.utility.Encryption;
import org.example.ostheo_projet.utility.Window;

public class EditAccountController {
    @FXML public PasswordField inputPassword;
    @FXML public PasswordField inputNewPassword;
    @FXML public TextField inputUsername;
    @FXML public Button btnUsername;
    @FXML public Button btnPassword;
    @FXML public Label lblMessageUsername;
    @FXML public Label lblMessagePassword;
    @FXML private VBox usernameView;
    @FXML private VBox passwordView;

    // SERVICES
    private PractitionerService practitionerService;
    private ApplicationContext applicationContext;

    public EditAccountController() {}

    public void initialize() {
        this.applicationContext = ServiceLocator.INSTANCE.getApplicationContext();

        btnUsername.setOnMouseClicked(mouseEvent -> {
            btnUsername.getStyleClass().removeAll("menu-button-inactive");
            btnUsername.getStyleClass().add("menu-button-active");

            btnPassword.getStyleClass().removeAll("menu-button-active");
            btnPassword.getStyleClass().add("menu-button-inactive");
        });

        btnPassword.setOnMouseClicked(mouseEvent -> {
            btnPassword.getStyleClass().removeAll("menu-button-inactive");
            btnPassword.getStyleClass().add("menu-button-active");

            btnUsername.getStyleClass().removeAll("menu-button-active");
            btnUsername.getStyleClass().add("menu-button-inactive");
        });

    }
    public void initService(PractitionerService practitionerService) {
        this.practitionerService = practitionerService;

        //Initialize fields with user credential data
        Practitioner userPractitioner = this.applicationContext.getCurrentPractitioner();
        if(userPractitioner == null){
            // Relocate user to connection scene
            Stage stage = Window.createOrShowWindow("connexion",this, "Connexion", "connexion.fxml", false, 600, 500, false);
            // Set the lblTitle text
            TextField lblTitle = (TextField) stage.getScene().lookup("#lblTitle");
            lblTitle.setText("Veuillez vous reconnecter pour modifier votre compte");

            //Close the current window
            inputPassword.getScene().getWindow().hide();
            return;
        }

        inputUsername.setText(userPractitioner.getUsername());
    }

    @FXML
    public void showUsernameTab() {
        usernameView.setVisible(true);
        usernameView.setManaged(true);
        passwordView.setVisible(false);
        passwordView.setManaged(false);
    }

    @FXML
    public void showPasswordTab() {
        usernameView.setVisible(false);
        usernameView.setManaged(false);
        passwordView.setVisible(true);
        passwordView.setManaged(true);
    }

    public void handleUpdateUsername(ActionEvent actionEvent) {
        lblMessageUsername.setVisible(false);

        String newUsername = inputUsername.getText();
        if(newUsername.replaceAll("\\s+","").isEmpty()){
            lblMessageUsername.setText("Veuillez saisir un nouveau nom de compte.");
            lblMessageUsername.setStyle("-fx-text-fill: red;");
            lblMessageUsername.setVisible(true);
            // Show error message
            return;
        }

        Practitioner userPractitioner = this.applicationContext.getCurrentPractitioner();
        userPractitioner.setUsername(newUsername);
        try {
            practitionerService.save(userPractitioner);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        lblMessageUsername.setText("Nom d'utilisateur mis à jour avec succès.");
        lblMessageUsername.setStyle("-fx-text-fill: green;");
        lblMessageUsername.setVisible(true);
    }

    public void handleUpdatePassword(ActionEvent actionEvent) {
        lblMessagePassword.setVisible(false);

        String currentPassword = inputPassword.getText();
        String newPassword = inputNewPassword.getText();
        if(newPassword.replaceAll("\\s+","").isEmpty() || currentPassword.replaceAll("\\s+","").isEmpty()){
            lblMessagePassword.setText("Veuillez remplir tous les champs.");
            lblMessagePassword.setStyle("-fx-text-fill: red;");
            lblMessagePassword.setVisible(true);
            return;
        }

        //Verify that the current pwd matches for the user credential
        Practitioner userPractitioner = this.applicationContext.getCurrentPractitioner();
        if(!Encryption.verifyPassword(currentPassword, userPractitioner.getPassword())){
            lblMessagePassword.setText("Mot de passe actuel incorrect.");
            lblMessagePassword.setStyle("-fx-text-fill: red;");
            lblMessagePassword.setVisible(true);
            return;
        }

        userPractitioner.setPassword(Encryption.hashString(newPassword));
        try {
            practitionerService.save(userPractitioner);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        lblMessagePassword.setText("Mot de passe mis à jour avec succès.");
        lblMessagePassword.setStyle("-fx-text-fill: green;");
        lblMessagePassword.setVisible(true);
    }
}
