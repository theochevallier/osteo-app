package org.example.ostheo_projet.ui.component;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.ostheo_projet.model.PaymentType;
import org.example.ostheo_projet.service.PaymentTypeService;
import org.example.ostheo_projet.service.ServiceLocator;
import org.example.ostheo_projet.utility.Utils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class PaymentTypeComponent extends VBox {
    @FXML
    private TextField inputPayment;
    @FXML
    private Button btnSavePayment;
    @FXML
    private Button btnArchivePayment;
    @FXML
    private Button btnDeletePayment;
    @FXML
    private Label lblPaymentMessage;
    @FXML
    private HBox conteneurComponent;

    public PaymentType paymentType;
    private final PaymentTypeService paymentTypeService;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PaymentTypeComponent.class);

    public PaymentTypeComponent() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/ostheo_projet/ui/view/payment-type-component.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        this.paymentTypeService = ServiceLocator.INSTANCE.getPaymentTypeService();

        try {
            loader.load();

            Utils.insertIconInButton(btnSavePayment, "/data/check.png", Color.WHITE, ContentDisplay.CENTER);
            Utils.insertIconInButton(btnArchivePayment, "/data/archiver.png", Color.BLACK, ContentDisplay.RIGHT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
        if (paymentType != null) {
            inputPayment.setText(paymentType.getName());
        } else {
            this.paymentType = new PaymentType();
            this.paymentType.setId(0);
        }

        if(this.paymentType.getId() != 0 && this.paymentType.getArchivedAt() != null){
            btnArchivePayment.setText("Désarchiver");
            btnSavePayment.setDisable(true);
            conteneurComponent.setStyle(conteneurComponent.getStyle() + "-fx-background-color: #605d5d; -fx-border-color: #282727;");
        } else {
            btnArchivePayment.setText("Archiver");
            btnSavePayment.setDisable(false);
            conteneurComponent.setStyle(conteneurComponent.getStyle() + "-fx-border-color: #007bff; -fx-background-color: #9dc8f8;");
        }

        // If payment type entity is related to zero invoice, allow the user to delete it in database
        boolean isPaymentTypeUsed = this.paymentTypeService.isPaymentTypeUsed(this.paymentType.getId());
        btnDeletePayment.setVisible(!isPaymentTypeUsed);
        btnDeletePayment.setManaged(!isPaymentTypeUsed);
    }

    public void handleSavePayment() {
        String paymentTypeLabel = inputPayment.getText();
        if (paymentTypeLabel == null || paymentTypeLabel.isEmpty()) {
            lblPaymentMessage.setText("Veuillez entrer un nom de type de paiement.");
            lblPaymentMessage.setVisible(true);
            lblPaymentMessage.setStyle("-fx-text-fill: red;");
            return;
        }


        this.paymentType.setName(paymentTypeLabel);
        try {
            this.paymentTypeService.save(this.paymentType);
            lblPaymentMessage.setText("Le type de paiement a été sauvegardé avec succès.");
            lblPaymentMessage.setVisible(true);
            lblPaymentMessage.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            String message;
            if(e.getMessage().contains("already exists")){
                message = e.getMessage();
            } else {
                message = "Une erreur est survenue lors de la sauvegarde du type de paiement, veuillez consulter les logs.";
            }
            logger.error("Une erreur est survenue lors de la sauvegarde du type de paiement : {}", e.getMessage());
            lblPaymentMessage.setText(message);
            lblPaymentMessage.setVisible(true);
            lblPaymentMessage.setStyle("-fx-text-fill: red;");
            throw new RuntimeException(e);
        }
    }

    public void handleArchivePayment() {
        if(this.paymentType.getId() == 0){
            lblPaymentMessage.setText("Impossible d'archiver un type de paiement non sauvegardé.");
            lblPaymentMessage.setVisible(true);
            lblPaymentMessage.setStyle("-fx-text-fill: red;");
            return;
        }
        this.paymentTypeService.archive(this.paymentType);
        lblPaymentMessage.setText("Le type de paiement a été " + btnArchivePayment.getText().toLowerCase() + " avec succès.");
        lblPaymentMessage.setVisible(true);
        lblPaymentMessage.setStyle("-fx-text-fill: green;");
        setPaymentType(this.paymentType);
    }

    public void handleDeletePayment(){
        if(this.paymentType.getId() == 0){
            lblPaymentMessage.setText("Impossible de supprimer un type de paiement non sauvegardé.");
            lblPaymentMessage.setVisible(true);
            lblPaymentMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        this.paymentTypeService.delete(this.paymentType);
        lblPaymentMessage.setText("Le type de paiement a été supprimé avec succès.");
        lblPaymentMessage.setVisible(true);
        lblPaymentMessage.setStyle("-fx-text-fill: green;");
        setPaymentType(null);
    }
}
