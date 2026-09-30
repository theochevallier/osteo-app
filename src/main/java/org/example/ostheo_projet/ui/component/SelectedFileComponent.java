package org.example.ostheo_projet.ui.component;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.ostheo_projet.utility.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class SelectedFileComponent extends VBox {
    @FXML private Label lblFileName;
    @FXML private ImageView iconFile;
    @FXML private Button btnDeleteFile;

    public File selectedFile;
    public ObservableList<File> selectedFiles;

    public SelectedFileComponent(ObservableList<File> selectedFiles, boolean showDeleteButton){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/ostheo_projet/ui/view/selected-file-component.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        this.selectedFiles = selectedFiles;

        try {
            loader.load();

            btnDeleteFile.setVisible(showDeleteButton);
            btnDeleteFile.managedProperty().bind(btnDeleteFile.visibleProperty());

            setOnMouseEntered(event -> {
                lblFileName.setUnderline(true);
            });
            setOnMouseExited(event -> {
                lblFileName.setUnderline(false);
            });

            addListenerClickFileLabel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
        lblFileName.setText(selectedFile.getName());

        Image fileImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/data/file.png")));
        this.iconFile.setImage(fileImage);
    }

    public void handleDeleteFile() {
        this.selectedFiles.remove(this.selectedFile);
    }

    public void addListenerClickFileLabel(){
        lblFileName.setOnMouseClicked(event -> {
            // Open the file
            Utils.openFile(selectedFile);
        });
    }


}
