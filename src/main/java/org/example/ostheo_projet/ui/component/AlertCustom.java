package org.example.ostheo_projet.ui.component;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.ostheo_projet.RootApplication;

import java.io.InputStream;

public class AlertCustom extends Alert {
    private static final String cssPath = "/style.css";

    public AlertCustom(String title, String message, Alert.AlertType alertType) {
        super(alertType);
        this.setTitle(title);
        this.setHeaderText(null);
        this.setContentText(message);
        this.getDialogPane().getStylesheets().add(RootApplication.class.getResource(cssPath).toExternalForm());
    }

    public AlertCustom(String title, String message, Alert.AlertType alertType, String iconPath) {
        // This permet d'appeler le constructeur parent de la classe
        this(title, message, alertType);
        if(iconPath == null || iconPath.isEmpty()) {
            return;
        }

        // Verify that the resource exists
        try {
            InputStream stream = AlertCustom.class.getResourceAsStream(iconPath);
            if(stream == null) return;

            Image icon = new Image(stream);
            ImageView imageView = new ImageView(icon);
            imageView.setFitHeight(48);
            imageView.setFitWidth(48);
            imageView.setPreserveRatio(true);

            this.getDialogPane().setGraphic(imageView);
        } catch (Exception e) {
            System.out.println("Exception in class AlertCustom finding icon : " + e);
        }

    }

    public void showAlert() {
        this.showAndWait();
    }
}
