package org.example.ostheo_projet.ui.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.ostheo_projet.Interface.Navigable;
import org.example.ostheo_projet.enums.ActionOnOpen;
import org.example.ostheo_projet.event.ConfigurationUpdatedEvent;
import org.example.ostheo_projet.event.ConsultationUpdatedEvent;
import org.example.ostheo_projet.model.Configuration;
import org.example.ostheo_projet.model.Consultation;
import org.example.ostheo_projet.model.ConsultationFilters;
import org.example.ostheo_projet.model.Patient;
import org.example.ostheo_projet.service.ConfigurationService;
import org.example.ostheo_projet.service.ConsultationService;
import org.example.ostheo_projet.service.PatientService;
import org.example.ostheo_projet.service.ServiceLocator;
import org.example.ostheo_projet.utility.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConsultationTableController extends VBox implements Navigable {

    // FXML ELEMENTS
    public Button btnAddConsult;
    public StackPane paneBtnAddConsult;
    public Button btnModifyConsult;
    public Button btnDeleteConsult;
    public Button btnViewAllConsults;
    public TableView<Consultation> consultTable;
    public HBox paneFiltersButtons;
    public TableColumn<Consultation, String> tableConsultColumnPatient;
    public TableColumn<Consultation, String> tableConsultColumnDate;
    public TableColumn<Consultation, String> tableConsultColumnReason;
    public TableColumn<Consultation, String> tableConsultColumnTreatment;
    public TableColumn<Consultation, String> tableConsultColumnNote;
    public TableColumn<Consultation, String> tableConsultColumnLastUpdate;

    public TextField inputFilterDate;
    public TextField inputFilterSearch;
    public Button btnResetFilters;

    // SERVICES
    public PatientService patientService;
    public ConsultationService consultationService;
    public NavigationManager navigationManager;
    public ConfigurationService configurationService;
    public ApplicationContext applicationContext;
    public EventManager eventManager;


    // LOCAL VARIABLES
    public String parentConteneur;
    public ObservableList<Consultation> observableConsultList;
    public List<Consultation> consultationList;
    public List<Consultation> consultationFilteredList;
    public Configuration configuration;
    public int nbMaxResultsTable;


    public ConsultationTableController() {
    }


    // INITIALIZATION METHODS

    public void initialize() {
        initButtonIcons();
        initServices();
        initFilterNodes();
        initContext();
    }

    public void initServices() {
        this.patientService = ServiceLocator.INSTANCE.getPatientService();
        this.consultationService = ServiceLocator.INSTANCE.getConsultationService();
        this.navigationManager = NavigationManager.getInstance();
        this.applicationContext = ServiceLocator.INSTANCE.getApplicationContext();
        this.configurationService = ServiceLocator.INSTANCE.getConfigurationService();
        this.configuration = this.configurationService.getByPractitioner(this.applicationContext.getCurrentPractitioner());
        this.eventManager = EventManager.getInstance();
    }

    public void initContext() {
        initConsultTable();
        fillConsultTable();
        filterConsultationTable(this.applicationContext.getCurrentPatient() != null ? this.applicationContext.getCurrentPatient() : null);
        listenerFiltersInputs();

        // Filter consultation table when a patient is selected in patient table view
        this.applicationContext.currentPatientProperty().addListener((observable, oldValue, newValue) -> {
            filterConsultationTable(newValue);
        });

        eventManager.subscribe(ConfigurationUpdatedEvent.class, event -> {
            this.configuration = event.configuration();
            this.nbMaxResultsTable = this.configuration.getMaxResultsFindAll();
            fillConsultTable();
        });

        btnAddConsult.disableProperty().bind(this.applicationContext.currentPatientProperty().isNull());
        btnViewAllConsults.visibleProperty().bind(
                this.applicationContext.currentPatientProperty().isNotNull()
                        .and(Bindings.size(this.consultTable.getItems()).greaterThan(0))
        );

        Tooltip.install(paneBtnAddConsult, new Tooltip("Sélectionner un patient pour créer une nouvelle consultation"));

        this.eventManager = EventManager.getInstance();
        eventManager.subscribe(ConsultationUpdatedEvent.class, event -> {
            Patient patient = this.applicationContext.getCurrentPatient();
            if (patient != null) {
                fillConsultTable();
                filterConsultationTable(patient);
            } else {
                fillConsultTable();
            }
        });
    }

    public void initFilterNodes() {
        Form.addListenerDateFormater(inputFilterDate, null);
    }

    public void initButtonIcons() {
        // Affect icons to buttons
        Utils.insertIconInButton(btnAddConsult, "/data/plus.png", Color.WHITE, ContentDisplay.RIGHT);
        Utils.insertIconInButton(btnModifyConsult, "/data/modify.png", Color.WHITE, ContentDisplay.RIGHT);
        Utils.insertIconInButton(btnDeleteConsult, "/data/delete.png", Color.WHITE, ContentDisplay.RIGHT);
        Utils.insertIconInButton(btnResetFilters, "/data/reset.png", Color.BLACK, ContentDisplay.RIGHT);
    }

    public void initConsultTable() {
        this.observableConsultList = FXCollections.observableArrayList();
        consultTable.setFixedCellSize(50);
        consultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        consultTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            if (newWidth.doubleValue() > 0) {
                consultTable.setColumnResizePolicy(
                        TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
                );
            }
        });

        this.tableConsultColumnPatient.setCellValueFactory(cellData -> {
            Patient patient = cellData.getValue().getPatient();
            String patientName = patient.getFirstname() + " " + patient.getLastname();
            return new SimpleStringProperty(patientName);
        });
        this.tableConsultColumnDate.setCellValueFactory(cellData -> {
            LocalDate consultDate = cellData.getValue().getConsultationDate();
            if (consultDate != null) {
                return new SimpleStringProperty(consultDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            return new SimpleStringProperty("");
        });
        this.tableConsultColumnReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        this.tableConsultColumnTreatment.setCellValueFactory(new PropertyValueFactory<>("treatment"));
        this.tableConsultColumnNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        this.tableConsultColumnLastUpdate.setCellValueFactory(cellData -> {
            LocalDateTime createdAt = cellData.getValue().getCreatedAt();
            LocalDateTime updatedAt = cellData.getValue().getUpdatedAt();
            LocalDateTime lastUpdate = updatedAt != null ? updatedAt : createdAt;
            return new SimpleStringProperty(lastUpdate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")));
        });

        // Event listeners on double click and on select of a consult
        handleDoubleClickConsult();
        handleSelectConsult();

        this.applicationContext.currentPatientProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                fillConsultTable();
            }
        });
    }

    public void fillConsultTable() {
        this.consultationList = this.nbMaxResultsTable != 0 ? consultationService.getAll(this.nbMaxResultsTable) : consultationService.getAll();
        observableConsultList.clear();
        if (this.consultationList == null || this.consultationList.isEmpty()) return;

        observableConsultList.addAll(this.consultationList);

        //Order consult list by consultation date desc
        observableConsultList.sort((consult1, consult2) -> consult2.getConsultationDate().compareTo(consult1.getConsultationDate()));
        consultTable.setItems(observableConsultList);
    }

    public void filterConsultationTable(Patient patient) {
        if (patient != null) {
            // Get consults by patient
            observableConsultList = this.consultationService.getByPatient(patient) != null ? FXCollections.observableArrayList(this.consultationService.getByPatient(patient)) : FXCollections.observableArrayList();
            observableConsultList.sort((consult1, consult2) -> consult2.getConsultationDate().compareTo(consult1.getConsultationDate()));

            consultationFilteredList = observableConsultList;
            consultTable.setItems(observableConsultList);
        }
    }

    public void filterConsultationTable(List<Consultation> consultations) {
        if (consultations != null && !consultations.isEmpty()) {
            ObservableList<Consultation> filteredConsultList = FXCollections.observableArrayList(consultations);
            consultTable.setItems(filteredConsultList);
        } else {
            consultTable.setItems(observableConsultList);
        }
    }

    private void resizeColumns() {

        double width = consultTable.getWidth();

        double fixedWidth =
                tableConsultColumnPatient.getWidth()
                        + tableConsultColumnDate.getWidth()
                        + tableConsultColumnLastUpdate.getWidth();

        double remaining = width - fixedWidth;

        double each = remaining / 3;

        tableConsultColumnReason.setPrefWidth(each);
        tableConsultColumnTreatment.setPrefWidth(each);
        tableConsultColumnNote.setPrefWidth(each);
    }

    public void saveTableFilters() {
        this.applicationContext.setConsultationFilters(new ConsultationFilters(inputFilterSearch.getText(), inputFilterDate.getText()));
    }

    public void fillConsultationTableFilters(ConsultationFilters consultationFilters) {
        if (consultationFilters != null) {
            inputFilterSearch.setText(consultationFilters.getSearchText());
            inputFilterDate.setText(consultationFilters.getDate());
            filterConsultationTableWithInputs();
        }
    }

    public void filterConsultationTableWithInputs() {
        // Date input must be empty or fully set (10 chars)
        if (!inputFilterDate.getText().isEmpty() && inputFilterDate.getText().length() < 10) return;

        LocalDate consultationDate = !inputFilterDate.getText().trim().isEmpty() && inputFilterDate.getText().length() == 10 ? LocalDate.parse(inputFilterDate.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null;
        List<Consultation> consultationsFound = this.consultationService.getByFullTextSearch(this.nbMaxResultsTable != 0 ? this.nbMaxResultsTable : 200, this.inputFilterSearch.getText(), consultationDate);
        filterConsultationTable(consultationsFound);
    }


    // OVERRIDE/IMPLEMENTS METHODS

    @Override
    public void onOpen(ActionOnOpen actionOnOpen) {
        Platform.runLater(this::resizeColumns);
    }

    @Override
    public void onClose() {

    }

    @Override
    public void setParentConteneur(String parentConteneur) {
        this.parentConteneur = parentConteneur;
    }

    // LISTENER BUTTONS ACTIONS (HANDLE)
    public void handleNewConsult(ActionEvent actionEvent) {
        this.navigationManager.navigateToComponent("consultation-split-patient-infos.fxml", ActionOnOpen.ADD, "consultation-infos", this.parentConteneur, true);
    }

    public void handleUpdateConsult(ActionEvent actionEvent) {
    }

    public void handleDeleteConsult(ActionEvent actionEvent) {
    }

    public void handleDoubleClickConsult() {
        consultTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                applicationContext.setCurrentConsultation(consultTable.getSelectionModel().getSelectedItem());

                List<Consultation> selectedConsultation = consultTable.getSelectionModel().getSelectedItems();
                // Show consult info component with consultation datas
                this.navigationManager.navigateToComponent("consultation-split-patient-infos.fxml", ActionOnOpen.SHOW, "consultation-info", this.parentConteneur, true, selectedConsultation);
            }
        });
    }

    public void handleSelectConsult() {
        consultTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnModifyConsult.setDisable(newValue == null);
            btnDeleteConsult.setDisable(newValue == null);
        });
    }

    public void handleViewAllConsults(ActionEvent actionEvent) {
        this.navigationManager.navigateToComponent("consultation-split-patient-infos.fxml", ActionOnOpen.SHOW, "consultation-info", this.parentConteneur, true, this.consultationFilteredList);
    }

    public void handleFilterTable(KeyEvent event) {
        filterConsultationTableWithInputs();
    }

    public void handleResetFilters(ActionEvent actionEvent) {
        inputFilterDate.setText("");
        inputFilterSearch.setText("");

        if (this.applicationContext.getCurrentPatient() != null) {
            filterConsultationTable(this.applicationContext.getCurrentPatient());
        } else {
            fillConsultTable();
        }
    }

    public void listenerFiltersInputs() {
        Form.forEachNodeOfType(paneFiltersButtons, TextField.class, textField -> {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                saveTableFilters();
            });
        });
    }
}
