package org.example.ostheo_projet.ui.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.ostheo_projet.RootApplication;
import org.example.ostheo_projet.enums.ActionOnOpen;
import org.example.ostheo_projet.enums.DirectoryType;
import org.example.ostheo_projet.enums.MessageType;
import org.example.ostheo_projet.event.InvoiceUpdatedEvent;
import org.example.ostheo_projet.model.*;
import org.example.ostheo_projet.service.ConfigurationService;
import org.example.ostheo_projet.service.InvoiceService;
import org.example.ostheo_projet.service.PaymentTypeService;
import org.example.ostheo_projet.service.ServiceLocator;
import org.example.ostheo_projet.utility.ApplicationContext;
import org.example.ostheo_projet.utility.EventManager;
import org.example.ostheo_projet.utility.Form;
import org.example.ostheo_projet.utility.Utils;
import org.slf4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;


public class InvoiceComponent extends VBox {
    // FXML NODES
    @FXML private VBox paneShowInvoice;
    @FXML private VBox paneShowInvoiceInfosCheque;
    @FXML private HBox paneActionButtonsEdit;
    @FXML private HBox paneActionButtonsShow;
    @FXML private ScrollPane scrollPane;
    @FXML private Label lblTitlePage;
    @FXML private Label lblLastnamePatientInvoice;
    @FXML private Label lblFirstnamePatientInvoice;
    @FXML private Label lblBirthdayPatientInvoice;
    @FXML private Label lblDateConsultInvoice;
    @FXML private Label lblReasonConsultInvoice;
    @FXML private Label lblAmountInvoice;
    @FXML private Label lblPaymentTypeInvoice;
    @FXML private Label lblChequeRefInvoice;
    @FXML private Label lblChequeDepositIdInvoice;
    @FXML private Label lblErrorMessageInvoice;
    @FXML private Button btnModifyInvoice;
    @FXML private VBox root;
    @FXML private Button btnShowInvoiceFinder;
    @FXML private Button btnGenerateInvoice;

    // Edit nodes
    @FXML private VBox paneEditInvoiceInfosCheque;
    @FXML private ToggleGroup amountToggleGroup;
    @FXML private ToggleButton toggle1;
    @FXML private ToggleButton toggle2;
    @FXML private ToggleButton toggle3;
    @FXML private ComboBox<String> comboPaymentType;
    @FXML private TextField inputCustomAmount;
    @FXML private TextField inputChequeRefInvoice;
    @FXML private AutoCompleteTextField inputChequeDepositIdInvoice;

    // SERVICES
    public PaymentTypeService paymentTypeService;
    public InvoiceService invoiceService;
    public ConfigurationService configurationService;
    public ApplicationContext applicationContext;
    public EventManager eventManager;
    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(InvoiceComponent.class);


    // LOCAL VARIABLES
    public List<PaymentType> paymentTypes;
    private Invoice invoice;
    private Consultation consultation;
    private final ObjectProperty<Boolean> isInvoiceClosed;
    private final String invoiceFilePath;
    private Configuration configuration;



    public InvoiceComponent(Invoice invoice, Consultation consultation) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/ostheo_projet/ui/view/invoice-component.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        this.invoice = invoice;
        this.consultation = consultation != null ? consultation : this.invoice != null ? this.invoice.getConsultation() : null;
        this.invoiceService = ServiceLocator.INSTANCE.getInvoiceService();
        this.eventManager = EventManager.getInstance();
        this.applicationContext = ServiceLocator.INSTANCE.getApplicationContext();
        this.configurationService = ServiceLocator.INSTANCE.getConfigurationService();
        this.isInvoiceClosed = new SimpleObjectProperty<>(false);

        this.invoiceFilePath = Utils.getPatientsFilesDirectory() + this.consultation.getPatient().getId() + "/" + this.consultation.getId() + "/" + DirectoryType.COMPTA.toString().toLowerCase();

        try {
            loader.load();

            initializeServices();
            initializeComponent();

            Practitioner practitioner = this.applicationContext.getCurrentPractitioner();
            if(practitioner != null){
                this.configuration = this.configurationService.getByPractitioner(practitioner);
                String lastUsedDepositId = this.configuration.getLastUsedChequeDeposit() != null ? this.configuration.getLastUsedChequeDeposit() : "";
                inputChequeDepositIdInvoice.getEntries().add(lastUsedDepositId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // INITIALIZATION METHODS
    public void initializeServices(){
        this.applicationContext = ServiceLocator.INSTANCE.getApplicationContext();
        this.paymentTypeService = ServiceLocator.INSTANCE.getPaymentTypeService();
    }

    public void initializeComponent(){
        Form.changeScrollSpeed(scrollPane, 0.2);

        this.paymentTypes = paymentTypeService.getAll();
        comboPaymentType.getItems().addAll(paymentTypes.stream().map(PaymentType::getName).toList());

        // Add a listener to the toggle group to show/hide the custom amount input
        this.amountToggleGroup = new ToggleGroup();
        this.toggle1.setToggleGroup(amountToggleGroup);
        this.toggle2.setToggleGroup(amountToggleGroup);
        this.toggle3.setToggleGroup(amountToggleGroup);
        amountToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                inputCustomAmount.setDisable(!(newValue != null && newValue.getUserData() != null && newValue.getUserData().equals("autre")));
            }
        });

        eventManager.subscribe(InvoiceUpdatedEvent.class, event -> {
            this.invoice = event.invoice();
            fillLabelsWithData();
        });

        // Icons
        Utils.insertIconInButton(btnModifyInvoice, "/data/modify.png", Color.WHITE, ContentDisplay.RIGHT);
        Utils.insertIconInButton(btnShowInvoiceFinder, "/data/folder.png", Color.WHITE, ContentDisplay.RIGHT);
        Utils.insertIconInButton(btnGenerateInvoice, "/data/generate.png", Color.WHITE, ContentDisplay.RIGHT);
    }

    // OTHER METHODS

    public ObjectProperty<Boolean> isInvoiceClosed() {
        return isInvoiceClosed;
    }

    public void changeScrollPolicy(boolean allowScroll){
        if(allowScroll){
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        } else {
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }
    }

    public void onOpen(ActionOnOpen actionOnOpen) {
        changeActionFormMode(actionOnOpen);
    }

    public void changeActionFormMode(ActionOnOpen actionOnOpen){

        switch (actionOnOpen) {
            case SHOW:
                manageVisibilityAndDataPanes(true, true);
                lblTitlePage.setText("Facture n° " + this.invoice.getInvoiceNumber());
                paneActionButtonsEdit.setVisible(false);
                paneActionButtonsShow.setVisible(true);
                break;
            case EDIT:
                manageVisibilityAndDataPanes(false, true);
                paneActionButtonsEdit.setVisible(true);
                paneActionButtonsShow.setVisible(false);
                lblTitlePage.setText("Modifier la facture n° " + this.invoice.getInvoiceNumber());
                break;
            case ADD:
                manageVisibilityAndDataPanes(false, false);
                paneActionButtonsEdit.setVisible(true);
                paneActionButtonsShow.setVisible(false);
                lblTitlePage.setText("Nouvelle facture");
                break;
        }
    }

    public void manageVisibilityAndDataPanes(boolean showMode, boolean fillItemsWithData){
        // If there is a current patient, show his infos in labels
        // Labels SHOW
        if(showMode && fillItemsWithData) {
            fillLabelsWithData();

        // Inputs EDIT
        } else if (!showMode && fillItemsWithData) {
            fillInputsWithData();
        }

        if(showMode) {
            btnModifyInvoice.setVisible(true);
            paneActionButtonsEdit.setVisible(false);
            paneShowInvoice.setVisible(true);
            paneShowInvoice.setManaged(true);
            paneEditInvoiceInfosCheque.setVisible(false);
            paneEditInvoiceInfosCheque.setManaged(false);
            paneShowInvoiceInfosCheque.setVisible(true);
            paneShowInvoiceInfosCheque.setManaged(true);
        } else {
            btnModifyInvoice.setVisible(false);
            paneActionButtonsEdit.setVisible(true);
            paneEditInvoiceInfosCheque.setVisible(true);
            paneEditInvoiceInfosCheque.setManaged(true);
            paneShowInvoiceInfosCheque.setVisible(false);
            paneShowInvoiceInfosCheque.setManaged(false);

            fillPatientConsultLabelsWithData();
        }
    }

    public void fillLabelsWithData(){
        fillPatientConsultLabelsWithData();

        lblAmountInvoice.setText(String.format("%.2f €", this.invoice.getAmount()));
        lblPaymentTypeInvoice.setText(this.invoice.getPaymentType().getName());
        lblChequeRefInvoice.setText(this.invoice.getChequeRef() != null ? !this.invoice.getChequeRef().isEmpty() ? this.invoice.getChequeRef() : "N/A" : "N/A");
        lblChequeDepositIdInvoice.setText(this.invoice.getChequeDepositId() != null ? !this.invoice.getChequeDepositId().isEmpty() ? this.invoice.getChequeDepositId() : "N/A" : "N/A");
    }

    public void fillPatientConsultLabelsWithData() {
        Patient patient = this.applicationContext.getCurrentPatient();
        if(this.invoice == null && patient == null && this.consultation != null){
            patient = this.consultation.getPatient();
        } else {
            patient = this.invoice != null ? this.invoice.getConsultation().getPatient() : this.applicationContext.getCurrentPatient();
        }
        if (patient == null) return;

        lblFirstnamePatientInvoice.setText(patient.getFirstname());
        lblLastnamePatientInvoice.setText(patient.getLastname());
        lblBirthdayPatientInvoice.setText(patient.getBirthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        Consultation consultation = this.invoice != null ? this.invoice.getConsultation() : this.consultation;
        lblDateConsultInvoice.setText(consultation.getConsultationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblReasonConsultInvoice.setText(consultation.getReason());
    }

    public void fillInputsWithData(){
        fillPatientConsultLabelsWithData();

        if(this.invoice == null) return;

        comboPaymentType.getSelectionModel().select(this.invoice.getPaymentType().getName());

        // Select the toggle button corresponding to the invoice amount if it matches one of the predefined amounts, otherwise select "autre" and fill the custom amount input
        if (this.invoice.getAmount() == 30.0) {
            toggle1.setSelected(true);
        } else if (this.invoice.getAmount() == 55.0) {
            toggle2.setSelected(true);
        } else {
            toggle3.setSelected(true);
            inputCustomAmount.setDisable(false);
            inputCustomAmount.setText(String.format("%.2f", this.invoice.getAmount()));
        }

        inputChequeRefInvoice.setText(this.invoice.getChequeRef() != null ? this.invoice.getChequeRef() : "");
        inputChequeDepositIdInvoice.setText(this.invoice.getChequeDepositId() != null ? this.invoice.getChequeDepositId() : "");
    }

    public void saveInvoiceToDatabase(){
        // Get the selected amount
        String selectedToggle = amountToggleGroup.getSelectedToggle().getUserData().toString();
        double amount = 0.0;
        amount = Objects.equals(selectedToggle, "autre") ? Double.parseDouble(inputCustomAmount.getText().replace(",", ".")) : Double.parseDouble(selectedToggle);

        String payment = comboPaymentType.getValue();
        PaymentType paymentType = paymentTypes.stream().filter(pt -> pt.getName().equals(payment)).findFirst().orElseThrow(() -> new RuntimeException("Payment type not found"));
        String chequeRef = inputChequeRefInvoice.getText();
        String chequeDepositId = inputChequeDepositIdInvoice.getText();
        Invoice invoice = new Invoice(this.invoice != null ? this.invoice.getId() : 0, amount, chequeRef, chequeDepositId,
                paymentType, this.invoice != null ? this.invoice.getConsultation() : this.consultation);

        // Save cheque deposit id to the last used column in configuration
        if(this.configuration != null){
            this.configuration.setLastUsedChequeDeposit(chequeDepositId);
            this.configurationService.save(this.configuration);
        }

        try {
            this.invoiceService.save(invoice);
            this.invoice = invoice;
            org.example.ostheo_projet.utility.Logger.getInstance().showMessagePopUp("Facture enregistrée avec succès.", MessageType.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getInvoiceFile(){
        List<File> fileList = Utils.getFilesOfDirectory(new File(this.invoiceFilePath));
        if(fileList != null && !fileList.isEmpty()){
            return fileList.getFirst();
        }
        return null;
    }

    // HANDLE LISTENERS METHODS
    public void handleCancelModifyInvoice(){
        if(this.invoice == null){
            handleCloseInvoice();
        }
        changeActionFormMode(ActionOnOpen.SHOW);
    }

    public void handleSaveInvoice(){
        saveInvoiceToDatabase();
        changeActionFormMode(ActionOnOpen.SHOW);
        eventManager.publish(new InvoiceUpdatedEvent(this.invoice));
    }

    public void handleUpdateInvoice(){
        changeActionFormMode(ActionOnOpen.EDIT);
    }

    public void handleCloseInvoice(){
        this.root.setVisible(false);
        this.root.setManaged(false);
        this.isInvoiceClosed.set(true);
    }

    public void handleGeneratePDFInvoice(){
        lblErrorMessageInvoice.setVisible(false);

        // Check if the invoice is already generated
        File invoiceFile = getInvoiceFile();
        boolean isAlreadyGenerated = invoiceFile != null && invoiceFile.exists();
        AtomicBoolean continueProcess = new AtomicBoolean(!isAlreadyGenerated);
        if (isAlreadyGenerated) {
            String alertTitle = "Information";
            String alertMessage = "Un fichier PDF existe déjà pour cette facture. Que voulez-vous faire ? (Attention, la re-génération écrasera le fichier existant)";
            AlertCustom alert = new AlertCustom(alertTitle, alertMessage, Alert.AlertType.INFORMATION, "/data/icon-info.png");
            alert.getDialogPane().getStylesheets().add(RootApplication.class.getResource("/style.css").toExternalForm());
            //Set les boutons personnalisés juste avant le listener (évite bug de récupération de la réponse)
            ButtonType btnOpenFinder = new ButtonType("Finder", ButtonBar.ButtonData.YES);
            ButtonType btnGenerate = new ButtonType("Re-générer", ButtonBar.ButtonData.YES);
            ButtonType btnCancel = new ButtonType("Annuler", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(btnOpenFinder, btnGenerate, btnCancel);

            // Apply style class to buttons : Open finder
            Button btn = (Button) alert.getDialogPane().lookupButton(btnGenerate);
            Utils.insertIconInButton(btn, "/data/generate.png", Color.WHITE, ContentDisplay.RIGHT);
            btn.getStyleClass().add("green-button");

            // Generate
            btn = (Button) alert.getDialogPane().lookupButton(btnOpenFinder);
            Utils.insertIconInButton(btn, "/data/folder.png", Color.WHITE, ContentDisplay.RIGHT);
            btn.getStyleClass().add("blue-button");

            // Cancel
            btn = (Button) alert.getDialogPane().lookupButton(btnCancel);
            btn.getStyleClass().add("cancel-button");

            alert.showAndWait().ifPresent(response -> {
                if (response == btnOpenFinder) {
                    Utils.openFile(invoiceFile.getParentFile());
                } else if (response == btnGenerate) {
                    continueProcess.set(true);
                    try {
                        Files.deleteIfExists(invoiceFile.toPath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        // If user clicked on cancel or open finder, stop the process
        if(!continueProcess.get()){
            return;
        }

        if(this.invoice == null){
            org.example.ostheo_projet.utility.Logger.getInstance().showMessagePopUp("Veuillez d'abord enregistrer la facture avant de générer le PDF.", MessageType.INFO);
            return;
        }

        String fileName = "/" + this.invoice.getInvoiceNumber() + "_" + this.invoice.getConsultation().getPatient().getLastname() + ".pdf";
        try {
            File newInvoiceFile = this.invoiceService.generateInvoicePDF(this.invoice, this.invoiceFilePath + fileName);
            if (!newInvoiceFile.exists()) {
                // If there is an error during generation, and if there was a PDF already generated, write it again
                if (invoiceFile != null && invoiceFile.exists()) {
                    Files.copy(invoiceFile.toPath(), newInvoiceFile.toPath());
                }
            }
            Utils.openFile(newInvoiceFile);
        } catch (Exception e) {
            logger.error("Failed to generate PDF invoice: {}", e.getMessage());
            org.example.ostheo_projet.utility.Logger.getInstance().showMessagePopUp("Erreur lors de la génération du PDF.", MessageType.ERROR);
        }
    }

    public void handleShowInvoiceInFinder(){
        File invoiceDir = new File(this.invoiceFilePath);
        if (invoiceDir.exists() && invoiceDir.isDirectory()) {
            try {
                Desktop.getDesktop().open(invoiceDir);
            } catch (IOException e) {
                logger.error("Failed to open directory: {}", this.invoiceFilePath);
                logger.error("Error message: {}", e.getMessage());
                org.example.ostheo_projet.utility.Logger.getInstance().showMessagePopUp("Erreur lors de l'ouverture du répertoire de la facture, veuillez consulter les logs.", MessageType.ERROR);
            }
        }
    }

}
