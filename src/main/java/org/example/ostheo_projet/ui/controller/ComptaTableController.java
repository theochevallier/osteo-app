package org.example.ostheo_projet.ui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.ostheo_projet.Interface.Navigable;
import org.example.ostheo_projet.enums.ActionOnOpen;
import org.example.ostheo_projet.event.ConfigurationUpdatedEvent;
import org.example.ostheo_projet.event.InvoiceUpdatedEvent;
import org.example.ostheo_projet.model.*;
import org.example.ostheo_projet.service.*;
import org.example.ostheo_projet.ui.component.InvoiceComponent;
import org.example.ostheo_projet.utility.ApplicationContext;
import org.example.ostheo_projet.utility.EventManager;
import org.example.ostheo_projet.utility.NavigationManager;
import org.example.ostheo_projet.utility.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ComptaTableController extends VBox implements Navigable {
    // FXML NODES
    public Button btnAddInvoice;
    public Button btnModifyInvoice;
    public Button btnDeleteInvoice;
    public TableView<Invoice> comptaTable;
    public TableColumn<Invoice, String> tableComptaColumnInvoiceNumber;
    public TableColumn<Invoice, String> tableComptaColumnPatient;
    public TableColumn<Invoice, String> tableComptaColumnConsultDate;
    public TableColumn<Invoice, String> tableComptaColumnConsultReason;
    public TableColumn<Invoice, String> tableComptaColumnAmount;
    public TableColumn<Invoice, String> tableComptaColumnPaymentType;
    public TableColumn<Invoice, String> tableComptaColumnChequeRef;
    public TableColumn<Invoice, String> tableComptaColumnChequeDepositId;
    public VBox cardViewInvoice;
    public Label labelTotalInvoices;
    public Label labelTotalAmount;
    public ComboBox<String> comboFilterConsultDateMonth;
    public ComboBox<String> comboFilterConsultDateYear;
    public ComboBox<String> comboFilterPaymentType;
    public TextField inputFilterChequeDepositId;
    public Button btnResetFilters;


    // SERVICES
    public PatientService patientService;
    public ConsultationService consultationService;
    public InvoiceService invoiceService;
    public PaymentTypeService paymentTypeService;
    public NavigationManager navigationManager;
    public ConfigurationService configurationService;
    public ApplicationContext applicationContext;
    public EventManager eventManager;


    // LOCAL VARIABLES
    public ObservableList<Invoice> observableInvoiceList;
    public List<Invoice> invoiceList;
    public Configuration configuration;
    public static final ArrayList<String> months = new ArrayList<>(List.of("Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"));


    public ComptaTableController() {}

    // INITIALIZATION METHODS
    public void initialize() {
        initServices();
        initButtonIcons();
        initContext();
    }

    public void initServices() {
        this.patientService = ServiceLocator.INSTANCE.getPatientService();
        this.consultationService = ServiceLocator.INSTANCE.getConsultationService();
        this.invoiceService = ServiceLocator.INSTANCE.getInvoiceService();
        this.navigationManager = NavigationManager.getInstance();
        this.configurationService = ServiceLocator.INSTANCE.getConfigurationService();
        this.paymentTypeService = ServiceLocator.INSTANCE.getPaymentTypeService();
        this.applicationContext = ServiceLocator.INSTANCE.getApplicationContext();
        this.eventManager = EventManager.getInstance();
        this.configuration = this.configurationService.getByPractitioner(this.applicationContext.getCurrentPractitioner());
    }

    public void initContext() {
        initConsultTable();
        initFilterNodes();
        fillConsultTable();
        filterConsultationTableByPatient(this.applicationContext.getCurrentPatient());
        filterComptaTableByPredicates();

        // Filter consultation table when a patient is selected in patient table view
        this.applicationContext.currentPatientProperty().addListener((observable, oldValue, newValue) -> {
            filterConsultationTableByPatient(newValue);
        });
        this.applicationContext.currentConsultationProperty().addListener((observable, oldValue, newValue) -> {
            btnAddInvoice.setDisable(newValue == null);
        });

        eventManager.subscribe(InvoiceUpdatedEvent.class, event -> {
            this.invoiceList = invoiceService.getAll();
            fillConsultTable();
            filterConsultationTableByPatient(this.applicationContext.getCurrentPatient());
        });

        eventManager.subscribe(ConfigurationUpdatedEvent.class, event -> {
            this.configuration = event.configuration();
            fillConsultTable();
        });
    }

    public void initButtonIcons(){
        // Affect icons to buttons
        Utils.insertIconInButton(btnAddInvoice, "/data/plus.png", Color.WHITE, ContentDisplay.RIGHT);
        Utils.insertIconInButton(btnModifyInvoice, "/data/modify.png", Color.WHITE, ContentDisplay.RIGHT);
        Utils.insertIconInButton(btnDeleteInvoice, "/data/delete.png", Color.WHITE, ContentDisplay.RIGHT);
    }

    public void initConsultTable() {
        this.observableInvoiceList = FXCollections.observableArrayList();
        this.comptaTable.setFixedCellSize(50);
        this.tableComptaColumnInvoiceNumber.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));
        this.tableComptaColumnPatient.setCellValueFactory(cellData -> {
            Patient patient = cellData.getValue().getConsultation().getPatient();
            String patientName = patient.getFirstname() + " " + patient.getLastname() + " né le " + patient.getBirthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return new SimpleStringProperty(patientName);
        });
        this.tableComptaColumnConsultDate.setCellValueFactory(cellData -> {
            Consultation consultation = cellData.getValue().getConsultation();
            String consultDate = consultation.getConsultationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return new SimpleStringProperty(consultDate);
        });
        this.tableComptaColumnConsultReason.setCellValueFactory(cellData -> {
            Consultation consultation = cellData.getValue().getConsultation();
            String consultReason = consultation.getReason();
            return new SimpleStringProperty(consultReason);
        });
        this.tableComptaColumnAmount.setCellValueFactory(cellData -> {
            String amount = String.valueOf(cellData.getValue().getAmount().intValue()) + "€";
            return new SimpleStringProperty(amount);
        });
        this.tableComptaColumnPaymentType.setCellValueFactory(cellData -> {
            PaymentType paymentType = cellData.getValue().getPaymentType();
            return new SimpleStringProperty(paymentType.getName());
        });
        this.tableComptaColumnChequeRef.setCellValueFactory(new PropertyValueFactory<>("chequeRef"));
        this.tableComptaColumnChequeDepositId.setCellValueFactory(new PropertyValueFactory<>("chequeDepositId"));

        // Event listeners on double click and on select of a consult
        handleDoubleClickConsult();
        handleSelectConsult();

        comptaTable.itemsProperty().addListener((observable, oldValue, newValue) -> {
            updateTotalInvoicesLabels();
        });
    }

    public void initFilterNodes() {
        ObservableList<String> months = FXCollections.observableArrayList(
                "", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        );
        ObservableList<String> years = FXCollections.observableArrayList();
        years.add("");
        int currentYear = LocalDateTime.now().getYear();
        for (int i = 2021; i <= currentYear; i++) {
            years.add(String.valueOf(i));
        }
        ObservableList<String> paymentTypes = FXCollections.observableArrayList();
        List<PaymentType> paymentTypeList = this.paymentTypeService.getAll();
        for (PaymentType paymentType : paymentTypeList) {
            paymentTypes.add(paymentType.getName());
        }

        comboFilterConsultDateMonth.getItems().addAll(months);
        comboFilterConsultDateYear.getItems().addAll(years);
        comboFilterPaymentType.getItems().addAll(paymentTypes);

        comboFilterConsultDateYear.setValue(String.valueOf(currentYear));
    }


    // OTHER METHODS
    public void fillConsultTable() {
        int maxResults = this.configuration.getMaxResultsFindAll();
        this.invoiceList = invoiceService.getAll(maxResults);
        observableInvoiceList.clear();
        if (this.invoiceList == null || this.invoiceList.isEmpty()) return;

        observableInvoiceList.addAll(this.invoiceList);

        // Order consult list by consultation date desc
//        observableConsultList.sort((consult1, consult2) -> consult2.getConsultationDate().compareTo(consult1.getConsultationDate()));
        comptaTable.setItems(observableInvoiceList);
    }

    public void filterConsultationTableByPatient(Patient patient){
        if(patient != null) {
            FilteredList<Invoice> filteredConsultList = new FilteredList<>(observableInvoiceList, consult -> true);
            // Filter on patient id
            filteredConsultList.setPredicate(invoice -> invoice.getConsultation().getPatient().getId() == patient.getId());
            comptaTable.setItems(filteredConsultList);
        } else {
            comptaTable.setItems(observableInvoiceList);
        }
    }

    public void filterComptaTableByPredicates(){
        // Consultation month
        String selectedMonthString = comboFilterConsultDateMonth.getValue();
        int selectedMonthIndex = 0;
        if(selectedMonthString != null && !selectedMonthString.isEmpty()) {
            selectedMonthIndex = months.indexOf(selectedMonthString);
            selectedMonthIndex ++;
        }

        // Consultation year
        String selectedYearString = comboFilterConsultDateYear.getValue();
        int selectedYear = 0;
        if(selectedYearString != null && !selectedYearString.isEmpty()) {
            selectedYear = Integer.parseInt(selectedYearString);
        }

        // Invoice payment type
        String selectedPaymentTypeString = comboFilterPaymentType.getValue();
        PaymentType selectedPaymentType = null;
        if(selectedPaymentTypeString != null) {
            selectedPaymentType = this.paymentTypeService.getByName(selectedPaymentTypeString);
        }

        // Invoice cheque deposit id
        String chequeDepositId = inputFilterChequeDepositId.getText();

        List<Invoice> invoicesFound = this.invoiceService.getByPredicates(selectedMonthIndex, selectedYear, selectedPaymentType, chequeDepositId);
        if(invoicesFound != null && !invoicesFound.isEmpty()){
            comptaTable.setItems(FXCollections.observableArrayList(invoicesFound));
        } else {
            comptaTable.setItems(FXCollections.observableArrayList());
        }
    }

    public void updateTotalInvoicesLabels(){
        int countInvoices = comptaTable.getItems().size();
        int amountInvoices = comptaTable.getItems().stream().mapToInt(invoice -> invoice.getAmount().intValue()).sum();
        labelTotalInvoices.setText(String.valueOf(countInvoices));
        labelTotalAmount.setText(amountInvoices + "€");
        System.out.println("Total invoices: " + countInvoices + ", Total amount: " + amountInvoices + "€");
    }

    public ObservableList<Invoice> getComptaTableData(){
        return comptaTable.getItems();
    }

    // IMPLEMENTS METHODS

    @Override
    public void onOpen(ActionOnOpen actionOnOpen) {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void setParentConteneur(String parentConteneur) {

    }


    // LISTENER BUTTONS ACTIONS (HANDLE)
    public void handleDoubleClickConsult(){
        comptaTable.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2){
                InvoiceComponent invoiceComponent = new InvoiceComponent(comptaTable.getSelectionModel().getSelectedItem(), null);
                invoiceComponent.onOpen(ActionOnOpen.SHOW);
                cardViewInvoice.getChildren().clear();
                cardViewInvoice.getChildren().add(invoiceComponent);
            }
        });
    }

    public void handleSelectConsult(){
        comptaTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnModifyInvoice.setDisable(newValue == null);
            btnDeleteInvoice.setDisable(newValue == null);
        });
    }

    public void handleNewInvoice(ActionEvent actionEvent) {
        if(this.applicationContext.getCurrentConsultation() == null) {
            return;
        }
        InvoiceComponent invoiceComponent = new InvoiceComponent(null, this.applicationContext.getCurrentConsultation());
        invoiceComponent.onOpen(ActionOnOpen.ADD);
        cardViewInvoice.getChildren().clear();
        cardViewInvoice.getChildren().add(invoiceComponent);
    }

    public void handleUpdateInvoice(ActionEvent actionEvent) {
        InvoiceComponent invoiceComponent = new InvoiceComponent(comptaTable.getSelectionModel().getSelectedItem(), null);
        invoiceComponent.onOpen(ActionOnOpen.EDIT);
        cardViewInvoice.getChildren().clear();
        cardViewInvoice.getChildren().add(invoiceComponent);
    }

    public void handleDeleteInvoice(ActionEvent actionEvent) {
    }

    public void handleSelectComboConsultDateMonth(ActionEvent actionEvent) {
        filterComptaTableByPredicates();
    }

    public void handleSelectComboConsultDateYear(ActionEvent actionEvent) {
        filterComptaTableByPredicates();
    }

    public void handleSelectComboPaymentType(ActionEvent actionEvent) {
        filterComptaTableByPredicates();
    }

    public void handleFilterTableDepositId(KeyEvent keyEvent) {
        filterComptaTableByPredicates();
    }

    public void handleResetFilters(ActionEvent actionEvent) {
        comboFilterConsultDateMonth.setValue(null);
        comboFilterConsultDateYear.setValue(null);
        comboFilterPaymentType.setValue(null);
        fillConsultTable();
    }
}
