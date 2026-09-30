package org.example.ostheo_projet.ui.controller;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import org.example.ostheo_projet.Interface.Navigable;
import org.example.ostheo_projet.enums.ActionOnOpen;
import org.example.ostheo_projet.model.Consultation;
import org.example.ostheo_projet.model.Invoice;
import org.example.ostheo_projet.model.Patient;
import org.example.ostheo_projet.service.ConsultationService;
import org.example.ostheo_projet.service.InvoiceService;
import org.example.ostheo_projet.service.ServiceLocator;
import org.example.ostheo_projet.ui.component.ConsultationInfosComponent;
import org.example.ostheo_projet.ui.component.InvoiceComponent;
import org.example.ostheo_projet.utility.ApplicationContext;
import org.example.ostheo_projet.utility.Form;
import org.example.ostheo_projet.utility.NavigationManager;
import org.example.ostheo_projet.utility.Utils;

import java.util.ArrayList;
import java.util.List;

public class ConsultationSplitPatientInfosController extends VBox implements Navigable {
    // FXML ELEMENTS
    public SplitPane splitPaneConsult;
    public VBox conteneurConsultation;
    public VBox conteneurInvoice;
    public ScrollPane scrollPanePatient;
    public ScrollPane scrollPaneConsultation;
    public VBox conteneurPatientInfos;
    public VBox conteneurConsultationLinkButtons;
    public Button btnShowConsultBtnLinks;
    public AnchorPane anchorLinkButtons;
    public Button btnChangeSplitDivider;
    public VBox paneInvoice;
    public Label lblPaneInvoiceMessage;
    public Button btnCreateInvoice;
    public VBox paneCreateShowInvoice;
    public Button btnShowPatientInfosComponent;
    public Button btnShowInvoiceComponent;


    // SERVICES
    public ApplicationContext applicationContext;
    public ConsultationService consultationService;
    public NavigationManager navigationManager;
    public InvoiceService invoiceService;


    // LOCAL VARIABLES
    public String parentConteneur;
    private List<Consultation> consultationList;
    public List<Button> consultationLinkButtons;
    public BooleanProperty showInvoiceMode = new SimpleBooleanProperty(false);
    public BooleanProperty showPatientInfosMode = new SimpleBooleanProperty(true);

    public ConsultationSplitPatientInfosController() {
    }

    // INITIALIZATION METHODS
    public void initialize() {
        initServices();

        // Insert patient infos component
        this.navigationManager.addPaneConteneur("conteneurPatientInfos", conteneurPatientInfos);

        this.consultationLinkButtons = new ArrayList<>();

        initComponents();
    }

    public void initServices() {
        this.applicationContext = ServiceLocator.INSTANCE.getApplicationContext();
        this.consultationService = ServiceLocator.INSTANCE.getConsultationService();
        this.invoiceService = ServiceLocator.INSTANCE.getInvoiceService();
        this.navigationManager = NavigationManager.getInstance();
    }

    public void initComponents() {
        // Augment the scroll speed of the scroll pane
        Form.changeScrollSpeed(scrollPanePatient, 0.2);
        Form.changeScrollSpeed(scrollPaneConsultation, 0.2);

        conteneurPatientInfos.requestLayout();
        splitPaneConsult.setDividerPositions(0.65);
        splitPaneConsult.requestLayout();

        // Set the anchor to the middle of the frame
        setLinkButtonsNodeMiddleYFrame();

        Utils.insertIconInButton(btnShowConsultBtnLinks, "/data/three-dots.png", Color.WHITE, ContentDisplay.RIGHT);
        Utils.insertIconInButton(btnChangeSplitDivider, "/data/arrow-right.png", Color.WHITE, ContentDisplay.RIGHT);

        paneCreateShowInvoice.visibleProperty().bind(this.showInvoiceMode.not());
        paneInvoice.visibleProperty().bind(this.showPatientInfosMode.not());
        conteneurInvoice.visibleProperty().bind(this.showInvoiceMode);
        paneCreateShowInvoice.managedProperty().bind(paneCreateShowInvoice.visibleProperty());
        paneInvoice.managedProperty().bind(paneInvoice.visibleProperty());
        conteneurInvoice.managedProperty().bind(conteneurInvoice.visibleProperty());

        conteneurInvoice.visibleProperty().bind(this.showPatientInfosMode.not());
        conteneurPatientInfos.visibleProperty().bind(this.showPatientInfosMode);
        conteneurInvoice.managedProperty().bind(conteneurInvoice.visibleProperty());
        conteneurPatientInfos.managedProperty().bind(conteneurPatientInfos.visibleProperty());
    }

    public void initConsultationComponent(ActionOnOpen actionOnOpen) {
        navigateToPatientInfos();
        this.showPatientInfosMode.set(true);
        this.showInvoiceMode.set(false);

        switch (actionOnOpen) {
            case SHOW:
                constructLinksButtonComponent();
                showMenuButtons(true);
                break;
            case EDIT:
                constructAConsultationComponent(this.conteneurConsultation, ActionOnOpen.EDIT, this.consultationList.get(0));
                showMenuButtons(true);
                break;
            case ADD:
                constructAConsultationComponent(this.conteneurConsultation, ActionOnOpen.ADD, null);
                showMenuButtons(false);
                break;
        }

        // If view is in show all consultation for the patient, dont show the invoice button
        boolean showInvoiceButton = this.consultationList != null && this.consultationList.size() == 1;
        btnShowInvoiceComponent.setVisible(showInvoiceButton);
        btnShowInvoiceComponent.setManaged(showInvoiceButton);
    }


    // OTHER METHODS

    public void setLinkButtonsNodeMiddleYFrame() {
        double frameMiddleHeight = Screen.getPrimary().getVisualBounds().getHeight() / 2;
        AnchorPane.setTopAnchor(anchorLinkButtons, frameMiddleHeight - 100.0);
    }

    public ConsultationInfosComponent constructAConsultationComponent(VBox conteneur, ActionOnOpen actionOnOpen, Consultation consult) {
        ConsultationInfosComponent consultationInfosComponent = new ConsultationInfosComponent(consult);
        consultationInfosComponent.onOpen(actionOnOpen);

        conteneur.getChildren().add(consultationInfosComponent);

        if (this.consultationList != null && this.consultationList.size() > 1) {
            Separator separator = new Separator();
            separator.setStyle("-fx-padding: 40 0 60 0;");

            conteneur.getChildren().add(separator);
        }

        return consultationInfosComponent;
    }

    public void constructLinksButtonComponent() {
        this.conteneurConsultationLinkButtons.getChildren().clear();

        for (Consultation consultation : consultationList) {
            ConsultationInfosComponent consultationComponent = constructAConsultationComponent(this.conteneurConsultation, ActionOnOpen.SHOW, consultation);

            // Create the incr consult button
            Button btn = new Button(String.valueOf(consultation.getConsultationNumber()));
            btn.getStyleClass().add("traitement-number-button");
            btn.setStyle("-fx-min-width: 40; -fx-min-height: 40; -fx-opacity: 0.7");

            // When user clicks, scroll to the consultation component
            btn.setOnAction(e -> scrollToTraitement(consultationComponent));

            this.consultationLinkButtons.add(btn);
        }
    }

    private void scrollToTraitement(ConsultationInfosComponent component) {
        // Calculer la position du composant dans le conteneur
        Bounds bounds = component.localToScene(component.getBoundsInLocal());
        Bounds scrollBounds = scrollPaneConsultation.getContent().localToScene(scrollPaneConsultation.getContent().getBoundsInLocal());

        // Distance du haut du ScrollPane au composant
        double distance = bounds.getMinY() - scrollBounds.getMinY();
        double contentHeight = scrollPaneConsultation.getContent().getBoundsInLocal().getHeight();
        double viewportHeight = scrollPaneConsultation.getViewportBounds().getHeight();

        // Calculer la position du scroll (entre 0 et 1)
        double vvalue = distance / (contentHeight - viewportHeight);
        vvalue = Math.max(0, Math.min(1, vvalue)); // Clamp entre 0 et 1

        // Scroller vers le composant
        scrollPaneConsultation.setVvalue(vvalue);
    }

    public void showMenuButtons(boolean show) {
        btnShowInvoiceComponent.setVisible(show);
        btnShowInvoiceComponent.setManaged(show);
    }

    public void handleInvoiceClose(InvoiceComponent invoiceComponent) {
        invoiceComponent.isInvoiceClosed().addListener((observable, oldValue, newValue) -> {
            if (newValue == true) {
                this.showInvoiceMode.set(false);
            }
        });
    }

    public void navigateToPatientInfos() {
        Patient patient = this.consultationList != null && !this.consultationList.isEmpty() ? this.consultationList.get(0).getPatient() : this.applicationContext.getCurrentPatient();
        this.navigationManager.navigateToComponent("patient-infos.fxml", ActionOnOpen.SHOW, "patient-infos", "conteneurPatientInfos", false, patient);
    }


    // OVERRIDE/IMPLEMENTS METHODS

    @Override
    public void onOpen(ActionOnOpen actionOnOpen) {
        initConsultationComponent(actionOnOpen);

        // If there is more than one consultation, show the button to show the incremented consultation buttons
        if (this.consultationList != null && this.consultationList.size() > 1) {
            anchorLinkButtons.setVisible(true);
            addListenerOnShowConsultButtonsHover();
        }
    }

    @Override
    public void onOpen(Object entity) {
        this.consultationList = (List<Consultation>) entity;
    }

    @Override
    public void onClose() {

    }

    @Override
    public void setParentConteneur(String parentConteneur) {
        this.parentConteneur = parentConteneur;
    }


    // LISTENER BUTTONS ACTIONS (HANDLE)


    public void handleChangeSplitDivider(ActionEvent actionEvent) {
        if (splitPaneConsult.getDividerPositions()[0] >= 0.9) {
            splitPaneConsult.setDividerPositions(0.65);
        } else {
            splitPaneConsult.setDividerPositions(1.0);
        }
    }

    public void addListenerOnShowConsultButtonsHover() {
        // When user hovers button, it shows the conteneur with the incremented nb consultation buttons
        anchorLinkButtons.setOnMouseEntered(event -> {
            this.conteneurConsultationLinkButtons.getChildren().clear();
            this.conteneurConsultationLinkButtons.getChildren().addAll(this.consultationLinkButtons);

            double newY = anchorLinkButtons.getLayoutY() - (this.conteneurConsultationLinkButtons.getLayoutBounds().getHeight() / 2);
            AnchorPane.setTopAnchor(anchorLinkButtons, newY);

            this.conteneurConsultationLinkButtons.setVisible(true);
            this.conteneurConsultationLinkButtons.setManaged(true);

            btnShowConsultBtnLinks.setVisible(false);
            event.consume();
        });

        anchorLinkButtons.setOnMouseExited(event -> {
            this.conteneurConsultationLinkButtons.setVisible(false);
            this.conteneurConsultationLinkButtons.setManaged(false);
            this.conteneurConsultationLinkButtons.getChildren().clear();

            setLinkButtonsNodeMiddleYFrame();

            btnShowConsultBtnLinks.setVisible(true);
            event.consume();
        });
    }

    public void handleShowInvoiceComponent() {
        this.showPatientInfosMode.set(false);
        this.showInvoiceMode.set(false);

        btnShowPatientInfosComponent.getStyleClass().removeAll("menu-button-active");
        btnShowPatientInfosComponent.getStyleClass().add("menu-button-inactive");
        btnShowInvoiceComponent.getStyleClass().removeAll("menu-button-inactive");
        btnShowInvoiceComponent.getStyleClass().add("menu-button-active");

        List<Invoice> existingInvoice = this.invoiceService.getByConsultation(this.consultationList.get(0));
        if (!existingInvoice.isEmpty()) {
            showInvoice();
            btnCreateInvoice.setVisible(false);
            btnCreateInvoice.setManaged(false);
        } else {
            lblPaneInvoiceMessage.setText("Aucune facture existante pour ce patient.");
            btnCreateInvoice.setVisible(true);
            btnCreateInvoice.setManaged(true);
        }
    }

    public void handleShowPatientInfosComponent(ActionEvent actionEvent) {
        conteneurInvoice.getChildren().clear();
        btnShowInvoiceComponent.getStyleClass().removeAll("menu-button-active");
        btnShowInvoiceComponent.getStyleClass().add("menu-button-inactive");
        btnShowPatientInfosComponent.getStyleClass().removeAll("menu-button-inactive");
        btnShowPatientInfosComponent.getStyleClass().add("menu-button-active");
        this.showPatientInfosMode.set(true);
        this.showInvoiceMode.set(false);
        navigateToPatientInfos();
    }

    public void handleCreateInvoice(ActionEvent actionEvent) {
        this.showInvoiceMode.set(true);
        InvoiceComponent invoiceComponent = new InvoiceComponent(null, this.consultationList.get(0));
        invoiceComponent.onOpen(ActionOnOpen.ADD);
        handleInvoiceClose(invoiceComponent);
        this.conteneurInvoice.getChildren().clear();
        this.conteneurInvoice.getChildren().add(invoiceComponent);
    }

    public void showInvoice() {
        this.showInvoiceMode.set(true);
        Invoice invoice = this.invoiceService.getByConsultation(this.consultationList.get(0)).get(0);
        InvoiceComponent invoiceComponent = new InvoiceComponent(invoice, null);
        invoiceComponent.onOpen(ActionOnOpen.SHOW);
        invoiceComponent.changeScrollPolicy(false);
        handleInvoiceClose(invoiceComponent);
        this.conteneurInvoice.getChildren().clear();
        this.conteneurInvoice.getChildren().add(invoiceComponent);
    }
}
