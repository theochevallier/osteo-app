package org.example.ostheo_projet.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.ostheo_projet.Interface.EntityObserver;
import org.example.ostheo_projet.event.PractionerUpdatedEvent;
import org.example.ostheo_projet.model.Configuration;
import org.example.ostheo_projet.model.PaymentType;
import org.example.ostheo_projet.model.Practitioner;
import org.example.ostheo_projet.service.ConfigurationService;
import org.example.ostheo_projet.service.PaymentTypeService;
import org.example.ostheo_projet.service.PractitionerService;
import org.example.ostheo_projet.service.ServiceLocator;
import org.example.ostheo_projet.ui.component.PaymentTypeComponent;
import org.example.ostheo_projet.utility.*;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static org.example.ostheo_projet.utility.Form.forEachNodeOfType;

public class ConfigureAppController implements EntityObserver {
    public TextField inputDefaultPathFileConsult;
    public Button btnOpenFinderDefaultConsultFilePath;
    public Button btnGeneralInfos;
    public Button btnPracticien;
    public VBox paneCabinetInfos;
    public VBox panePracticienInfos;
    public VBox paneGeneralInfos;
    public Label lblMessageGeneralInfos;
    public TextField inputFirstnamePracticien;
    public TextField inputLastnamePracticien;
    public TextField inputEmailPracticien;
    public TextField inputPhonePracticien;
    public TextField inputLabelPracticien;
    public TextField inputRPPSNumber;
    public TextField inputCabinetName;
    public TextField inputInvoiceConsultLabel;
    public TextField inputAddressCabinet;
    public TextField inputPostalCodeCabinet;
    public TextField inputCityCabinet;
    public TextField inputCountryCabinet;
    public Button btnCabinet;
    public VBox paneLeftMenu;
    public TextField inputUsernameCredential;
    public PasswordField inputPassword;
    public PasswordField inputNewPassword;
    public Label lblMessageCredentialInfos;
    public VBox paneCredentialInfos;
    public Label lblMessagePracticienInfos;
    public Label lblMessageCabinetInfos;
    public Button btnPaymentType;
    public VBox panePaymentTypes;
    public VBox conteneurPaymentTypes;
    public Button btnAddPayment;
    public TextField inputMaxTableResults;
    public TextField inputInvoiceEndText;
    public VBox conteneurRoot;

    // SERVICES
    private PractitionerService practitionerService;
    private ConfigurationService configurationService;
    private ApplicationContext applicationContext;
    private EventManager eventManager;

    // LOCAL VARIABLES
    private File defaultConsultFilePath;
    private Configuration configuration;
    private Practitioner practitioner;
    private static final Logger logger = LoggerFactory.getLogger(ConfigureAppController.class);
    private List<PaymentType> paymentTypes;
    private PaymentTypeService paymentTypeService;
    private String currentMenuSelected = "Général";

    public ConfigureAppController() {}

    // INITIALIZATION METHODS
    public void initialize() {
        initService();
        initForm();
    }
    public void initService() {
        this.applicationContext = ServiceLocator.INSTANCE.getApplicationContext();
        this.practitionerService = ServiceLocator.INSTANCE.getCredentialService();
        this.configurationService = ServiceLocator.INSTANCE.getConfigurationService();
        this.paymentTypeService = ServiceLocator.INSTANCE.getPaymentTypeService();
        this.eventManager = EventManager.getInstance();

        this.practitioner = this.applicationContext.getCurrentPractitioner();
    }

    public void initForm(){
        Utils.insertIconInButton(btnOpenFinderDefaultConsultFilePath, "/data/folder.png", Color.WHITE, ContentDisplay.RIGHT);
        Utils.insertIconInButton(btnAddPayment, "/data/plus.png", Color.WHITE, ContentDisplay.RIGHT);

        Form.addListenerNumbersFormater(inputMaxTableResults);

        addClickListenersOnMenuButtons();
        createConfigurationIfNotExists();
        initNodesWithData();

        eventManager.subscribe(PractionerUpdatedEvent.class, event -> {
            applicationContext.setCurrentPractitioner(event.practioner());
            this.practitioner = event.practioner();
            initNodesWithData();
        });

        this.paymentTypes = this.paymentTypeService.getAll(true);
        fillConteneurPaymentTypes();

        Form.initInputFormatters(inputLastnamePracticien, inputFirstnamePracticien);
        listenerInputNodes();
    }

    public void fillConteneurPaymentTypes(){
        // If payment types list is empty, just show the new payment type button
        // If it is not empty, insert the payment types component in the conteneur
        conteneurPaymentTypes.getChildren().clear();
        if(this.paymentTypes == null || this.paymentTypes.isEmpty()) return;

        for(PaymentType paymentType : this.paymentTypes){
            PaymentTypeComponent paymentTypeComponent = new PaymentTypeComponent();
            paymentTypeComponent.setPaymentType(paymentType);
            conteneurPaymentTypes.getChildren().add(paymentTypeComponent);
        }
    }

    public void createConfigurationIfNotExists(){
        this.configuration = this.configurationService.getByPractitioner(this.practitioner);
        if(this.configuration == null) {
            Configuration newConfig = new Configuration();
            newConfig.setPractitioner(this.practitioner);
            this.configurationService.save(newConfig);
            this.configuration = newConfig;
            System.out.println("Nouvelle configuration créé avec succès pour l'utilisateur " + this.practitioner.getUsername() + ".");
        }
    }

    public void addClickListenersOnMenuButtons(){
        paneLeftMenu.getChildren().forEach(node -> {
            if(node instanceof Button btn){
                btn.setOnMouseClicked(mouseEvent -> {
                    paneLeftMenu.getChildren().forEach(btn2 -> {
                        if(btn2 instanceof Button btn2_ && btn2 != btn){
                            btn2_.getStyleClass().removeAll("menu-button-active");
                            btn2_.getStyleClass().add("menu-button-inactive");
                        } else if(btn2 instanceof Button btn2_) {
                            btn2_.getStyleClass().removeAll("menu-button-inactive");
                            btn2_.getStyleClass().add("menu-button-active");
                            this.currentMenuSelected = btn2_.getText();
                        }
                    });
                });
            }
        });
    }

    public void initNodesWithData(){
        // GENERAL INFOS
        if(this.practitioner == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Enregistrement de la configuration impossible, votre profil practicien n'a pas été trouvé. Veuillez vous reconnecter.");
            alert.showAndWait();
            return;
        }

        // GENERAL INFOS
        inputDefaultPathFileConsult.setText(this.configuration.getDefaultConsultFilePath());
        inputMaxTableResults.setText(String.valueOf(this.configuration.getMaxResultsFindAll()));

        // ACCOUNT INFOS
        inputUsernameCredential.setText(this.practitioner.getUsername());

        // PRACTICIEN INFOS
        inputFirstnamePracticien.setText(this.practitioner.getFirstname());
        inputLastnamePracticien.setText(this.practitioner.getLastname());
        inputEmailPracticien.setText(this.practitioner.getEmail());
        inputPhonePracticien.setText(this.practitioner.getPhone());
        inputRPPSNumber.setText(this.practitioner.getRppsId());
        inputLabelPracticien.setText(this.practitioner.getInvoicePractitionerLabel());

        // CABINET INFOS
        inputCabinetName.setText(this.configuration.getCabinetName());
        inputInvoiceConsultLabel.setText(this.configuration.getInvoiceConsultationLabel());
        inputAddressCabinet.setText(this.configuration.getCabinetAddress());
        inputPostalCodeCabinet.setText(this.configuration.getCabinetPostalCode());
        inputCityCabinet.setText(this.configuration.getCabinetCity());
        inputCountryCabinet.setText(this.configuration.getCabinetCountry());
        inputInvoiceEndText.setText(this.configuration.getInvoiceEndText());
    }

    // OTHER METHODS
    public void showPaneAndHideOthers(VBox paneToShow, VBox... panesToHide){
        paneToShow.setVisible(true);
        paneToShow.setManaged(true);
        for(VBox pane : panesToHide){
            pane.setVisible(false);
            pane.setManaged(false);
        }
    }

    // OVERRIDE / IMPLEMENTS METHODS

    @Override
    public <T> void stateChanged() {
        initNodesWithData();
    }


    // HANDLE LISTENER METHODS

    public boolean updateUsername() {
        lblMessageCredentialInfos.setVisible(false);

        String newUsername = inputUsernameCredential.getText();
        if(newUsername.replaceAll("\\s+","").isEmpty()){
            lblMessageCredentialInfos.setText("Veuillez saisir un nouveau nom de compte.");
            lblMessageCredentialInfos.setStyle("-fx-text-fill: red;");
            lblMessageCredentialInfos.setVisible(true);
            return false;
        }

        this.practitioner.setUsername(newUsername);
        try {
            practitionerService.save(this.practitioner);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        lblMessageCredentialInfos.setText("Informations du compte mises à jour avec succès.");
        lblMessageCredentialInfos.setStyle("-fx-text-fill: green;");
        lblMessageCredentialInfos.setVisible(true);
        return true;
    }

    public boolean updatePassword() {
        lblMessageCredentialInfos.setVisible(false);

        String currentPassword = inputPassword.getText();
        String newPassword = inputNewPassword.getText();
        if(newPassword.replaceAll("\\s+","").isEmpty() || currentPassword.replaceAll("\\s+","").isEmpty()){
            lblMessageCredentialInfos.setText("Veuillez remplir tous les champs.");
            lblMessageCredentialInfos.setStyle("-fx-text-fill: red;");
            lblMessageCredentialInfos.setVisible(true);
            return false;
        }

        //Verify that the current pwd matches for the user credential
        if(!Encryption.verifyPassword(currentPassword, this.practitioner.getPassword())){
            lblMessageCredentialInfos.setText("Mot de passe actuel incorrect.");
            lblMessageCredentialInfos.setStyle("-fx-text-fill: red;");
            lblMessageCredentialInfos.setVisible(true);
            return false;
        }

        this.practitioner.setPassword(Encryption.hashString(newPassword));
        try {
            practitionerService.save(this.practitioner);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        lblMessageCredentialInfos.setText("Mot de passe mis à jour avec succès.");
        lblMessageCredentialInfos.setStyle("-fx-text-fill: green;");
        lblMessageCredentialInfos.setVisible(true);
        return true;
    }

    public void handleOpenFinderDefaultConsultFilePath(ActionEvent actionEvent) {
        List<File> selectedDirectory = Utils.openFileChooser("Choisir un dossier", new FileChooser.ExtensionFilter("Dossier", "*.*"), "/Users", true, false);
        if(selectedDirectory == null){
            return;
        }

        this.defaultConsultFilePath = selectedDirectory.getFirst();
        inputDefaultPathFileConsult.setText(selectedDirectory.getFirst().getAbsolutePath());
        handleUpdateGeneralInfos(new ActionEvent());
    }

    public void handleClickGeneralInfos(ActionEvent actionEvent) {
        showPaneAndHideOthers(paneGeneralInfos, panePracticienInfos, paneCabinetInfos, paneCredentialInfos, panePaymentTypes);
    }

    public void handleClickPracticien(ActionEvent actionEvent) {
        showPaneAndHideOthers(panePracticienInfos, paneGeneralInfos, paneCabinetInfos, paneCredentialInfos, panePaymentTypes);
    }

    public void handleClickCredentialInfos(ActionEvent actionEvent) {
        showPaneAndHideOthers(paneCredentialInfos, paneGeneralInfos, panePracticienInfos, paneCabinetInfos, panePaymentTypes);
    }

    public void handleClickCabinet(ActionEvent actionEvent){
        showPaneAndHideOthers(paneCabinetInfos, paneGeneralInfos, panePracticienInfos, paneCredentialInfos, panePaymentTypes);
    }

    public void handleClickPaymentType(ActionEvent actionEvent) {
        showPaneAndHideOthers(panePaymentTypes, paneGeneralInfos, panePracticienInfos, paneCabinetInfos, paneCredentialInfos);
    }

    public void listenerInputNodes() {
        forEachNodeOfType(conteneurRoot, TextField.class, textField -> {
            textField.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if(!newValue) {
                    inputChanged();
                }
            });
        });

        forEachNodeOfType(conteneurRoot, TextArea.class, textArea -> {
            textArea.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if(!newValue) {
                    inputChanged();
                }
            });
        });

        forEachNodeOfType(conteneurRoot, ComboBox.class, comboBox -> {
            comboBox.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if(!newValue) {
                    inputChanged();
                }
            });
        });

    }

    public void inputChanged() {
        System.out.println("Input changed, saving configuration...");
        switch (this.currentMenuSelected) {
            case "Général" -> handleUpdateGeneralInfos(new ActionEvent());
            case "Praticien" -> handleUpdatePracticienInfos(new ActionEvent());
            case "Compte" -> handleUpdateCredentialInfos(new ActionEvent());
            case "Cabinet" -> handleUpdateCabinetInfos(new ActionEvent());
        }
    }

    public void handleUpdateGeneralInfos(ActionEvent actionEvent) {
        lblMessageGeneralInfos.setVisible(false);

        String defaultConsultFilePath = inputDefaultPathFileConsult.getText();
        if(!defaultConsultFilePath.isEmpty()){
            this.defaultConsultFilePath = new File(inputDefaultPathFileConsult.getText());
            if(!this.defaultConsultFilePath.isDirectory()){
                lblMessageGeneralInfos.setText("Le dossier par défaut renseigné pour les fichiers de consultation n'est pas un dossier valide.");
                lblMessageGeneralInfos.setVisible(true);
                lblMessageGeneralInfos.setStyle("-fx-text-fill: red;");
                logger.error("Le dossier par défaut renseigné pour les fichiers de consultation n'est pas un dossier valide : {}", this.defaultConsultFilePath.getAbsolutePath());
                return;
            }
        }

        int maxResults = Integer.parseInt(inputMaxTableResults.getText());
        if(maxResults <= 0){
            lblMessageGeneralInfos.setText("Le nombre maximum de résultats à afficher doit être supérieur à 0.");
            lblMessageGeneralInfos.setVisible(true);
            lblMessageGeneralInfos.setStyle("-fx-text-fill: red;");
            logger.error("Le nombre maximum de résultats à afficher doit être supérieur à 0 : {}", maxResults);
            return;
        }

        this.configuration.setDefaultConsultFilePath(inputDefaultPathFileConsult.getText());
        this.configuration.setMaxResultsFindAll(maxResults);
        try {
            this.configurationService.save(this.configuration);
            lblMessageGeneralInfos.setText("Informations sauvegardées avec succès.");
            lblMessageGeneralInfos.setStyle("-fx-text-fill: green;");
            lblMessageGeneralInfos.setVisible(true);
        } catch (Exception e) {
            logger.error("Fenêtre configuration : Erreur lors de la sauvegarde de la configuration : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void handleUpdatePracticienInfos(ActionEvent actionEvent) {
        Practitioner practitioner = new Practitioner(this.practitioner != null ? this.practitioner.getId() : 0, inputFirstnamePracticien.getText(),
                inputLastnamePracticien.getText(), inputEmailPracticien.getText(), inputPhonePracticien.getText(), inputRPPSNumber.getText(), this.practitioner.getUsername(),
                this.practitioner.getPassword(), inputLabelPracticien.getText(), this.practitioner.getCreatedAt(), this.practitioner.getUpdatedAt(), this.practitioner.getArchivedAt());
        try {
            this.practitionerService.save(practitioner);
            lblMessagePracticienInfos.setVisible(true);
            lblMessagePracticienInfos.setStyle("-fx-text-fill: green;");
            lblMessagePracticienInfos.setText("Informations sauvegardées avec succès.");
        } catch (Exception e){
            logger.error("Erreur lors de la sauvegarde du profil practicien : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public void handleUpdateCredentialInfos(ActionEvent actionEvent) {
        if(!updateUsername()) return;
        updatePassword();
    }

    public void handleUpdateCabinetInfos(ActionEvent actionEvent) {
        this.configuration.setCabinetName(inputCabinetName.getText());
        this.configuration.setInvoiceConsultationLabel(inputInvoiceConsultLabel.getText());
        this.configuration.setCabinetAddress(inputAddressCabinet.getText());
        this.configuration.setCabinetPostalCode(inputPostalCodeCabinet.getText());
        this.configuration.setCabinetCity(inputCityCabinet.getText());
        this.configuration.setCabinetCountry(inputCountryCabinet.getText());
        this.configuration.setInvoiceEndText(inputInvoiceEndText.getText());
        try {
            this.configurationService.save(this.configuration);
            lblMessageCabinetInfos.setText("Informations sauvegardées avec succès.");
            lblMessageCabinetInfos.setStyle("-fx-text-fill: green;");
            lblMessageCabinetInfos.setVisible(true);
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde des informations du cabinet : {}", e.getMessage());
            lblMessageCabinetInfos.setText("Erreur lors de la sauvegarde des informations du cabinet, veuillez consulter les logs.");
            lblMessageCabinetInfos.setVisible(true);
            lblMessageCabinetInfos.setStyle("-fx-text-fill: red;");
            throw new RuntimeException(e);
        }
    }


    public void handleAddPaymentType(ActionEvent actionEvent) {
        PaymentTypeComponent paymentTypeComponent = new PaymentTypeComponent();
        paymentTypeComponent.setPaymentType(null);
        conteneurPaymentTypes.getChildren().add(paymentTypeComponent);
    }
}
