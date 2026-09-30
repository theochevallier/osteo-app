package org.example.ostheo_projet.ui.controller;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.ostheo_projet.event.ConsultationUpdatedEvent;
import org.example.ostheo_projet.event.PatientUpdatedEvent;
import org.example.ostheo_projet.model.Configuration;
import org.example.ostheo_projet.service.ConfigurationService;
import org.example.ostheo_projet.service.ServiceLocator;
import org.example.ostheo_projet.ui.component.InvoiceComponent;
import org.example.ostheo_projet.utility.ApplicationContext;
import org.example.ostheo_projet.utility.EventManager;
import org.example.ostheo_projet.utility.Utils;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ImportDialogController {
    public TextField inputDirectoryImportFiles;
    public Button btnOpenFinderDirectoryImportFiles;
    public Label lblImportStatus;

    // SERVICES
    private ConfigurationService configurationService;
    private ApplicationContext applicationContext;
    private Configuration configuration;
    private EventManager eventManager;

    // LOCAL VARIABLES
    private File importFileDirectory;
    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(ImportDialogController.class);


    // INITIALIZATION METHODS

    public ImportDialogController() {
        this.eventManager = EventManager.getInstance();
    }

    public void initialize() {
        this.applicationContext = ServiceLocator.INSTANCE.getApplicationContext();
        this.configurationService = ServiceLocator.INSTANCE.getConfigurationService();
        this.configuration = this.configurationService.getByPractitioner(this.applicationContext.getCurrentPractitioner());
    }

    // OTHERS METHODS

    // Principe : créer les tables PG si elles existent pas via script sql
    // importer les fichiers dans les tables temporaires PG
    // insérer les patients, updater les historiques medicales et les consultations ?
    // essayer de trouver les villes des patients, et leurs professions


    public void executeScript(String scriptPath, String args){
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", scriptPath, args);
            File directory = new File(System.getProperty("user.dir"));
            logger.info("Current working directory: {}", directory.getAbsolutePath());
            pb.directory(directory);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            String output = new String(process.getInputStream().readAllBytes());
            logger.info("Script output: {}", output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    // HANDLE EVENTS METHODS

    public void handleOpenFinderDirectoryImportFiles(ActionEvent actionEvent) {
        List<File> file = Utils.openFileChooser("Choisir un répertoire", null,System.getProperty("user.home") + "/Desktop/", true, false);
        if(file != null && !file.isEmpty()) {
            this.importFileDirectory = file.getFirst();
            inputDirectoryImportFiles.setText(importFileDirectory.getAbsolutePath());
        }
    }

    public void handleImport(ActionEvent actionEvent) throws InterruptedException {
        Path scriptPath = Paths.get(System.getProperty("user.home"), "Documents", "Ostheo", "Installation", "scripts", "script_import_csv.sh");
        if(!Files.exists(scriptPath)) {
            logger.error("Import script file not found, path : {}", scriptPath);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Le script d'importation est introuvable.");
            alert.showAndWait();
            return;
        }
        String script = scriptPath.toAbsolutePath().toString();
        String csvFilePath = inputDirectoryImportFiles.getText();
        logger.info("Script path: {}", script);
        logger.info("CSV file path: {}", csvFilePath);
        executeScript(script, csvFilePath);
        lblImportStatus.setText("Les données ont été importées avec succès.");
        eventManager.publish(new PatientUpdatedEvent(null));
        eventManager.publish(new ConsultationUpdatedEvent(null));

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            // fermeture de la fenêtre
            inputDirectoryImportFiles.getScene().getWindow().hide();
        });
        pause.play();
    }
}
