package org.example.ostheo_projet.ui.controller;

import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.ostheo_projet.Interface.Navigable;
import org.example.ostheo_projet.enums.ActionOnOpen;
import org.example.ostheo_projet.enums.MessageType;
import org.example.ostheo_projet.event.PatientUpdatedEvent;
import org.example.ostheo_projet.model.*;
import org.example.ostheo_projet.service.InvoiceService;
import org.example.ostheo_projet.service.ServiceLocator;
import org.example.ostheo_projet.utility.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MainWindowController implements Navigable {
    // FXML ELEMENTS
    public VBox rootConteneurPatient;
    public VBox rootConteneurConsultation;
    public VBox rootConteneurCompta;
    public VBox conteneurLabelTabPane;
    public Button btnBackPatient;
    public Button btnBackConsult;
    public Button btnBackCompta;
    public Button btnPopOutPatient;
    public Label lblCurrentPatientName;
    public TabPane tabPaneApplication;
    public Tab tabConsultation;
    public Tab tabCompta;
    public Tab tabPatient;
    public Button btnCloseMessagePopUp;
    public Label lblGeneralMessage;
    public HBox generalMessagePopUp;


    // SERVICES
    public NavigationManager navigationManager;
    public ApplicationContext applicationContext;
    public InvoiceService invoiceService;
    public EventManager eventManager;


    // LOCAL VARIABLES
    public final static Logger logger = LoggerFactory.getLogger(MainWindowController.class);


    public MainWindowController() {
    }

    // INITIALIZATION METHODS
    public void initialize() {
        initServices();
        initContext();
        initFrame();
    }

    public void initServices(){
        this.navigationManager = NavigationManager.getInstance();
        this.applicationContext = ServiceLocator.INSTANCE.getApplicationContext();
        this.invoiceService = ServiceLocator.INSTANCE.getInvoiceService();
        this.eventManager = EventManager.getInstance();
    }

    public void initContext(){
        this.navigationManager.addPaneConteneur("rootConteneurPatient", rootConteneurPatient);
        this.navigationManager.addPaneConteneur("rootConteneurConsultation", rootConteneurConsultation);
        this.navigationManager.addPaneConteneur("rootConteneurCompta", rootConteneurCompta);

        this.navigationManager.navigateToComponent("patient-table.fxml", ActionOnOpen.NONE, "patient-table", "rootConteneurPatient", false);
        this.navigationManager.navigateToComponent("consultation-table.fxml", ActionOnOpen.NONE, "consultation-table", "rootConteneurConsultation", false);
        this.navigationManager.navigateToComponent("compta-table.fxml", ActionOnOpen.NONE, "compta-table", "rootConteneurCompta", false);
    }

    public void initFrame(){
        this.applicationContext.currentPatientProperty().addListener((observable, oldValue, newValue) -> {
            setCurrentPatientName(newValue);
        });

        eventManager.subscribe(PatientUpdatedEvent.class, event -> {
            setCurrentPatientName(event.patient());
        });

        btnBackPatient.visibleProperty().bind(this.navigationManager.getCanPaneNavigateBack("rootConteneurPatient"));
        btnBackConsult.visibleProperty().bind(this.navigationManager.getCanPaneNavigateBack("rootConteneurConsultation"));
        btnBackCompta.visibleProperty().bind(this.navigationManager.getCanPaneNavigateBack("rootConteneurCompta"));
        btnPopOutPatient.visibleProperty().bind(this.applicationContext.currentPatientProperty().isNotNull());
        btnPopOutPatient.managedProperty().bind(btnPopOutPatient.visibleProperty());

        Utils.insertIconInButton(btnBackPatient, "/data/arrow-left.png", Color.WHITE, ContentDisplay.LEFT);
        Utils.insertIconInButton(btnBackCompta, "/data/arrow-left.png", Color.WHITE, ContentDisplay.LEFT);
        Utils.insertIconInButton(btnBackConsult, "/data/arrow-left.png", Color.WHITE, ContentDisplay.LEFT);

        setLabelToMiddleXFrame();

        tabPaneApplication.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            Tab selectedTab = tabPaneApplication.getSelectionModel().getSelectedItem();
            Node node = (Node) event.getTarget();
            while (node != null && !node.getStyleClass().contains("tab")) {
                node = node.getParent();
            }

            if (node != null && selectedTab.getText().equals("Patient")) {
                // If current tab is patient tab, save patient before navigating back
                PatientInfosController controller = (PatientInfosController) navigationManager.getControllerByFxmlName("patient-infos.fxml");
                if(controller != null) controller.inputChanged();
            }
        });
    }

    // OTHER METHODS
    public void setCurrentPatientName(Patient patient) {
        lblCurrentPatientName.setText(patient == null ? "Aucun patient sélectionné" : "Patient : " + patient.getFirstname() + " " + patient.getLastname());
    }

    public void setLabelToMiddleXFrame() {
        double frameMiddleWidth = Screen.getPrimary().getVisualBounds().getWidth() / 2;
        AnchorPane.setLeftAnchor(conteneurLabelTabPane, frameMiddleWidth - 20.0);
    }

    public void showMessagePopUp(String message, MessageType messageType) {
        String styleBase = "-fx-padding: 12 16; -fx-border-width: 2px; -fx-border-radius: 12px;";
        switch (messageType) {
            case SUCCESS -> generalMessagePopUp.setStyle(styleBase + "-fx-background-color: #4CAF50; -fx-border-color: #367c39;");
            case ERROR -> generalMessagePopUp.setStyle(styleBase + "-fx-background-color: #f44336; -fx-border-color: #b42c22;");
            case WARNING -> generalMessagePopUp.setStyle(styleBase + "-fx-background-color: #ff9800; -fx-border-color: #f44336;");
            case INFO -> generalMessagePopUp.setStyle(styleBase + "-fx-background-color: #008dff; -fx-border-color: #033c67");
        }

        lblGeneralMessage.setText(message);
        generalMessagePopUp.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        pause.setOnFinished(event -> {
            generalMessagePopUp.setVisible(false);
        });

        pause.play();
    }


    // OVERRIDE/IMPLEMENTS METHODS
    @Override
    public void onOpen(ActionOnOpen actionOnOpen) {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void setParentConteneur(String rootConteneur) {
    }

    // LISTENER BUTTONS ACTIONS (HANDLE)
    public void handleNavigateBackPatient(ActionEvent actionEvent){
        // Save patient before navigating back
        PatientInfosController patientInfoController = (PatientInfosController) navigationManager.getControllerByFxmlName("patient-infos.fxml");
        if(patientInfoController != null) {
            patientInfoController.inputChanged();
        }
        navigationManager.back("rootConteneurPatient");
    }

    public void handleNavigateBackConsult(ActionEvent actionEvent){
        this.applicationContext.setCurrentConsultation(null);
        navigationManager.back("rootConteneurConsultation");
    }

    public void handleNavigateBackCompta(ActionEvent actionEvent){
        navigationManager.back("rootConteneurCompta");
    }

    public void handlePopOutPatient(ActionEvent actionEvent) {
        this.applicationContext.setCurrentPatient(null);
    }

    public void showEditConfigureApp(ActionEvent actionEvent) {
        Stage stage = Window.createOrShowWindow("configure-app",this, "Configurer l'application", "configure-app.fxml", false, 800, 600, false);
    }

    public void handleExportComptaExcel(ActionEvent actionEvent) {
        // Récupérer toutes les données compta et les intégrer dans un fichier xls ou xlsx
        try {
            // Get the compta table data to export it
            ComptaTableController comptaTableController = (ComptaTableController) this.navigationManager.getControllerByFxmlName("compta-table.fxml");
            ObservableList<Invoice> data = comptaTableController.getComptaTableData();
            List<ExportCompta> exportData = invoiceService.convertInvoicesToExportCompta(data);

            invoiceService.exportDataToExcel(exportData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleImportData(ActionEvent actionEvent) {
        Stage stage = Window.createOrShowWindow("import-dialog",this, "Importer des données", "import-dialog.fxml", false, 800, 600, false);
    }

    public void saveDatabase(ActionEvent actionEvent) {
        try {
            Path scriptPath = Paths.get(System.getProperty("user.home"), "Documents", "Ostheo", "Installation", "scripts","script_backup.sh");
            Path applicationPath = Paths.get(System.getProperty("user.home"), "Documents", "Ostheo");
            if(!Files.exists(scriptPath)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Le script de sauvegarde de la base de données est introuvable.");
                alert.showAndWait();
                logger.error("The script that save database was not found.");
                return;
            }
            ProcessBuilder pb = new ProcessBuilder(scriptPath.toAbsolutePath().toString(), applicationPath.toAbsolutePath() + "/DATABASE_BACKUPS/");
            File directory = new File(applicationPath.toAbsolutePath().toString());
            pb.directory(directory);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            String output = new String(process.getInputStream().readAllBytes());
            logger.info("Save database script output : " + output);
            Alert.AlertType alertType = Alert.AlertType.INFORMATION;
            String message = "La sauvegarde de la base de données s'est terminée avec succès.";

            // TODO: ? If error occured, get the detail in shell logs and show in error alert, otherwise show info alert
            if(exitCode != 0) {
                alertType = Alert.AlertType.ERROR;
                message = "Une erreur est survenue lors de la sauvegarde de la base de données, veuillez consulter les logs pour plus d'informations.";
            }
            Alert alert = new Alert(alertType);
            alert.setTitle("Sauvegarde de la base de données");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void handleCloseMessagePopUp(ActionEvent actionEvent) {
    }
}
