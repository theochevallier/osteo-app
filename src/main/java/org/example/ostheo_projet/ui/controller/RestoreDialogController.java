package org.example.ostheo_projet.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.example.ostheo_projet.utility.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RestoreDialogController {
    @FXML public TextField inputSelectedFile;
    @FXML public Button btnOpenFolder;
    @FXML public Button btnRestore;

    public File selectedFile;

    public RestoreDialogController() {}

    public void closeWindow(){
        btnRestore.getScene().getWindow().hide();
    }

    @FXML
    public void handleChooseFile(ActionEvent actionEvent) {
        List<File> file = Utils.openFileChooser("Choisir un fichier de sauvegarde", new FileChooser.ExtensionFilter("Fichiers SQL", "*.sql"),
                System.getProperty("user.home") + "/Desktop/PGbackup/", false, false);
        if(file != null && !file.isEmpty()) {
            this.selectedFile = file.getFirst();
        }
        if(selectedFile != null) {
            inputSelectedFile.setText(selectedFile.getName());
            btnRestore.setDisable(false);
        } else {
            btnRestore.setDisable(true);
        }
    }

    @FXML
    public void handleRestore(ActionEvent actionEvent) {
        try {
            // TODO : put script path in variable
            ProcessBuilder pb = new ProcessBuilder("/Users/Theo_1/Desktop/script_restore.sh", selectedFile.getAbsolutePath());
            File directory = new File("/Users/Theo_1/Desktop");
            pb.directory(directory);
            Process process = pb.start();
            int exitCode = process.waitFor();
            String output = new String(process.getInputStream().readAllBytes());
            System.out.println("Script output: " + output);

            Alert.AlertType alertType = Alert.AlertType.INFORMATION;
            String message = "La restauration de la base de données s'est terminée avec succès.";

            // TODO: ? If error occurred, get the detail in shell logs and show in error alert, otherwise show info alert
            if(exitCode != 0) {
                alertType = Alert.AlertType.ERROR;
                message = "Une erreur est survenue lors de la restauration de la base de données, veuillez consulter les logs pour plus d'informations.";
            }
            Alert alert = new Alert(alertType);
            alert.setTitle("Restauration de la base de données");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();

            closeWindow();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
