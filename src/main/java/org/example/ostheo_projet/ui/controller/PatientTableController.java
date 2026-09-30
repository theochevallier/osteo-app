package org.example.ostheo_projet.ui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.example.ostheo_projet.Interface.Navigable;
import org.example.ostheo_projet.enums.ActionOnOpen;
import org.example.ostheo_projet.enums.Genre;
import org.example.ostheo_projet.enums.MessageType;
import org.example.ostheo_projet.event.ConfigurationUpdatedEvent;
import org.example.ostheo_projet.event.PatientUpdatedEvent;
import org.example.ostheo_projet.model.City;
import org.example.ostheo_projet.model.Configuration;
import org.example.ostheo_projet.model.Patient;
import org.example.ostheo_projet.model.PatientFilters;
import org.example.ostheo_projet.service.CityService;
import org.example.ostheo_projet.service.ConfigurationService;
import org.example.ostheo_projet.service.PatientService;
import org.example.ostheo_projet.service.ServiceLocator;
import org.example.ostheo_projet.ui.component.AutoCompleteTextField;
import org.example.ostheo_projet.utility.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class PatientTableController implements Navigable {

    // FXML ELEMENTS
    public TableView<Patient> patientTable;
    public Button btnRefreshTable;
    public Button btnAddPatient;
    public Button btnModifyPatient;
    public Button btnDeletePatient;
    public Button btnArchivePatient;
    public HBox paneTableFilters;

    public TableColumn<Patient, String> tablePatientColumnGenre;
    public TableColumn<Patient, String> tablePatientColumnName;
    public TableColumn<Patient, String> tablePatientColumnFirstname;
    public TableColumn<Patient, String> tablePatientColumnBirthday;
    public TableColumn<Patient, String> tablePatientColumnCity;
    public TableColumn<Patient, String> tablePatientColumnPostalCode;
    public TableColumn<Patient, String> tablePatientColumnDepartement;
    public TableColumn<Patient, String> tablePatientColumnEmail;
    public TableColumn<Patient, String> tablePatientColumnMobile;
    public TableColumn<Patient, String> tablePatientColumnLastUpdate;

    public ComboBox<Genre> comboFilterGenre;
    public TextField inputFilterLastname;
    public TextField inputFilterFirstname;
    public TextField inputFilterBirthday;
    public AutoCompleteTextField inputFilterAutoCompleteCity;
    public AutoCompleteTextField inputFilterPostalCode;
    public TextField inputFilterEmail;
    public TextField inputFilterPhone;


    // SERVICES
    public String parentConteneur;
    public NavigationManager navigationManager;
    public PatientService patientService;
    public CityService cityService;
    public ConfigurationService configurationService;
    public ApplicationContext applicationContext;
    public EventManager eventManager;

    // LOCAL VARIABLES
    public ObservableList<Patient> observablePatientList;
    public List<Patient> patientList;
    public List<City> cityList;
    public Configuration configuration;
    public int nbMaxResultsTable;


    public PatientTableController(){}

    // Initialization methods

    public void initialize() {
        initServices();
        initComponents();
    }

    public void initServices(){
        this.applicationContext = ServiceLocator.INSTANCE.getApplicationContext();
        this.navigationManager = NavigationManager.getInstance();
        this.patientService = ServiceLocator.INSTANCE.getPatientService();
        this.cityService = ServiceLocator.INSTANCE.getCityService();
        this.configurationService = ServiceLocator.INSTANCE.getConfigurationService();
        this.configuration = this.configurationService.getByPractitioner(this.applicationContext.getCurrentPractitioner());

        this.eventManager = EventManager.getInstance();
        eventManager.subscribe(PatientUpdatedEvent.class, event -> {
            // Update the patient list and refresh the table
            getNbMaxResultsAndRefreshTable();
        });
        eventManager.subscribe(ConfigurationUpdatedEvent.class, event -> {
            this.configuration = event.configuration();
            getNbMaxResultsAndRefreshTable();
        });
    }

    public void initComponents(){
        initPatientTable();
        initFilterNodes();
        initButtonIcons();
        initNodesListener();

//        this.applicationContext.currentPatientProperty().addListener((observable, oldValue, newValue) -> {
//            if(newValue != oldValue){
//                this.navigationManager.back("rootConteneurPatient");
//            }
//        });
    }

    public void initButtonIcons(){
        // Affect icons to buttons
        Utils.insertIconInButton(btnRefreshTable, "/data/reset.png", Color.WHITE, ContentDisplay.RIGHT);
        Utils.insertIconInButton(btnAddPatient, "/data/plus.png", Color.WHITE, ContentDisplay.RIGHT);
        Utils.insertIconInButton(btnModifyPatient, "/data/modify.png", Color.WHITE, ContentDisplay.RIGHT);
        Utils.insertIconInButton(btnDeletePatient, "/data/delete.png", Color.WHITE, ContentDisplay.RIGHT);
        Utils.insertIconInButton(btnArchivePatient, "/data/archiver.png", Color.BLACK, ContentDisplay.RIGHT);
    }

    public void initPatientTable(){
        this.observablePatientList = FXCollections.observableArrayList();
        tablePatientColumnGenre.setCellValueFactory(cellData -> {
            String genre = cellData.getValue().getGenre() != null ? cellData.getValue().getGenre().toString() : "";
            return new SimpleStringProperty(Utils.formatStringFirstLetterUppercase(genre));
        });
        tablePatientColumnName.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        tablePatientColumnFirstname.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        // Formater la LocalDate en String pour l'affichage dans la table
        tablePatientColumnBirthday.setCellValueFactory(cellData -> {
            LocalDate dateOfBirth = cellData.getValue().getBirthday();
            if (dateOfBirth != null) {
                return new SimpleStringProperty(dateOfBirth.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            return new SimpleStringProperty("");
        });
        tablePatientColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tablePatientColumnCity.setCellValueFactory(cellData -> {
            City city = cellData.getValue().getCity();
            if (city != null) {
                return new SimpleStringProperty(city.getName());
            }
            return new SimpleStringProperty("");
        });
        tablePatientColumnPostalCode.setCellValueFactory(cellData -> {
            City city = cellData.getValue().getCity();
            if (city != null) {
                return new SimpleStringProperty(city.getPostalCode());
            }
            return new SimpleStringProperty("");
        });
        tablePatientColumnMobile.setCellValueFactory(new PropertyValueFactory<>("mobilePhone"));
        tablePatientColumnDepartement.setCellValueFactory(cellData -> {
            City city = cellData.getValue().getCity();
            if (city != null && city.getDepartement() != null) {
                return new SimpleStringProperty(city.getDepartement().getName());
            }
            return new SimpleStringProperty("");
        });
        tablePatientColumnLastUpdate.setCellValueFactory(cellData -> {
            LocalDateTime createdAt = cellData.getValue().getCreatedAt();
            LocalDateTime updatedAt = cellData.getValue().getUpdatedAt();
            LocalDateTime lastUpdate = updatedAt != null ? updatedAt : createdAt;
            return new SimpleStringProperty(lastUpdate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")));
        });

        // Event listener on double click patient line and on selecting a patient
        handleDoubleClickPatient();
        handleSelectPatient();

        // Fill the table
        int maxResults = this.configuration.getMaxResultsFindAll();
        this.patientList = patientService.getAll(maxResults);
        fillPatientTable(this.patientList);
    }

    public void initFilterNodes(){
        // Combo genre
        Genre[] genres = Genre.values();
        comboFilterGenre.getItems().addAll(genres);

        // Autocomplete city
        this.cityList = cityService.getAll();
        inputFilterAutoCompleteCity.getEntries().addAll(this.cityList.stream().map(city -> city.getName() + ", " + city.getPostalCode()).toList());

        // Autocomplete postal code
        inputFilterPostalCode.getEntries().addAll(this.cityList.stream().map(City::getPostalCode).toList());

        // Birthday
        Form.addListenerDateFormater(inputFilterBirthday, null);
    }

    public void initNodesListener(){
        Form.forEachNodeOfType(paneTableFilters, TextField.class,  textField -> {
            textField.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if(!newValue) {
                    saveFilters();
                }
            });
        });
    }


    // OTHER METHODS

    public void fillPatientTable(List<Patient> patients){
        observablePatientList.clear();
        if(patients == null || patients.isEmpty()) return;

        observablePatientList.addAll(patients);
        patientTable.setItems(observablePatientList);
    }

    public void filterPatientTable(List<Patient> patientFilteredList){
        if(patientFilteredList == null || patientFilteredList.isEmpty()){
            patientTable.setItems(observablePatientList);
        } else {
            ObservableList<Patient> filteredObservableList = FXCollections.observableArrayList(patientFilteredList);
            patientTable.setItems(filteredObservableList);
        }
    }

    public void filterPatientTableWithFilters(){
        String cityName = inputFilterAutoCompleteCity.getText().contains(",") ? inputFilterAutoCompleteCity.getText().split(",")[0] : inputFilterAutoCompleteCity.getText();
        LocalDate birthday = !inputFilterBirthday.getText().trim().isEmpty() && inputFilterBirthday.getText().length() == 10 ? LocalDate.parse(inputFilterBirthday.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null;

        List<Patient> usersFound = patientService.getByCriteria(this.nbMaxResultsTable != 0 ? this.nbMaxResultsTable : 200, inputFilterLastname.getText(), inputFilterFirstname.getText(), birthday, cityName, inputFilterPostalCode.getText(),
                inputFilterPhone.getText(), "", inputFilterEmail.getText(), comboFilterGenre.getValue());

        filterPatientTable(usersFound);
    }

    public void getNbMaxResultsAndRefreshTable(){
        this.nbMaxResultsTable = this.configuration.getMaxResultsFindAll();
        this.patientList = patientService.getAll(this.nbMaxResultsTable);
        fillPatientTable(this.patientList);
    }

    public void saveFilters(){
        this.applicationContext.setPatientFilters(new PatientFilters(
                inputFilterLastname.getText(),
                inputFilterFirstname.getText(),
                inputFilterBirthday.getText(),
                inputFilterAutoCompleteCity.getText(),
                inputFilterPostalCode.getText(),
                inputFilterEmail.getText(),
                inputFilterPhone.getText(),
                comboFilterGenre.getValue() != null ? comboFilterGenre.getValue().toString() : ""
        ));
    }

    public void fillInputsFiltersAndFilterTable(PatientFilters patientFilters){
        if(patientFilters == null) return;

        inputFilterLastname.setText(patientFilters.getLastname());
        inputFilterFirstname.setText(patientFilters.getFirstname());
        inputFilterBirthday.setText(patientFilters.getBirthdate());
        inputFilterAutoCompleteCity.setText(patientFilters.getCity());
        inputFilterPostalCode.setText(patientFilters.getPostalCode());
        inputFilterEmail.setText(patientFilters.getEmail());
        inputFilterPhone.setText(patientFilters.getPhone());
        if(patientFilters.getGender() != null && !patientFilters.getGender().isEmpty()) {
            comboFilterGenre.setValue(Genre.valueOf(patientFilters.getGender()));
        }

        filterPatientTableWithFilters();
    }

    // IMPLEMENTATION AND OVERRIDES METHODS
    @Override
    public void onOpen(ActionOnOpen actionOnOpen) {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void setParentConteneur(String parentConteneur) {
        this.parentConteneur = parentConteneur;
    }

    // Event action methods
    public void handleNewPatient(ActionEvent actionEvent) {
        this.applicationContext.setCurrentPatient(null);
        this.navigationManager.navigateToComponent("patient-infos.fxml", ActionOnOpen.ADD, "patient-infos", this.parentConteneur, true);
        this.applicationContext.setIsPatientModifying(true);
    }

    public void handleUpdatePatient(ActionEvent actionEvent) {
        applicationContext.setCurrentPatient(patientTable.getSelectionModel().getSelectedItem());

        // Show patient info component with patient datas
        this.navigationManager.navigateToComponent("patient-infos.fxml", ActionOnOpen.EDIT, "patient-infos", this.parentConteneur, true);
    }

    public void handleDeletePatient(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Etes-vous surs de vouloir supprimer ce patient ? Toutes ses données associées seront supprimées également.");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            Patient patientToDelete = this.patientTable.getSelectionModel().getSelectedItem();
            this.patientService.delete(patientToDelete);
            this.patientList.remove(patientToDelete);
            patientTable.getItems().remove(patientToDelete);
            Logger.getInstance().showMessagePopUp("Patient supprimé avec succès.", MessageType.SUCCESS);
        }
    }

    public void handleDoubleClickPatient(){
        patientTable.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2 && patientTable.getSelectionModel().getSelectedItem() != null){
                applicationContext.setCurrentPatient(patientTable.getSelectionModel().getSelectedItem());

                // Show patient info component with patient datas
                this.navigationManager.navigateToComponent("patient-infos.fxml", ActionOnOpen.SHOW, "patient-infos", this.parentConteneur, true);

                // For the consultation table controler, go back to table view if a consultation view was opened
                this.navigationManager.back("rootConteneurConsultation");
            }
        });
    }

    public void handleSelectPatient(){
        patientTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnModifyPatient.setDisable(newValue == null);
            btnDeletePatient.setDisable(newValue == null);
            btnArchivePatient.setDisable(newValue == null);
        });
    }

    // TODO
    public void handleArchivePatient(ActionEvent actionEvent) {

    }

    public void handleFilterTable(KeyEvent actionEvent) {
        filterPatientTableWithFilters();
    }

    public void handleSelectComboGenre(ActionEvent actionEvent) {
        handleFilterTable(null);
    }

    public void handleRefreshTable(ActionEvent actionEvent) {
        getNbMaxResultsAndRefreshTable();
    }
}
