package org.example.ostheo_projet.ui.controller;


import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.example.ostheo_projet.Interface.Navigable;
import org.example.ostheo_projet.enums.ActionOnOpen;
import org.example.ostheo_projet.enums.Genre;
import org.example.ostheo_projet.enums.MessageType;
import org.example.ostheo_projet.enums.Osteoporose;
import org.example.ostheo_projet.event.PatientUpdatedEvent;
import org.example.ostheo_projet.model.*;
import org.example.ostheo_projet.service.CityService;
import org.example.ostheo_projet.service.JobService;
import org.example.ostheo_projet.service.PatientService;
import org.example.ostheo_projet.service.ServiceLocator;
import org.example.ostheo_projet.ui.component.AutoCompleteTextField;
import org.example.ostheo_projet.ui.component.OsteoporoseComponent;
import org.example.ostheo_projet.utility.*;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.example.ostheo_projet.utility.Form.forEachNodeOfType;

public class PatientInfosController extends VBox implements Navigable {
    // FXML ELEMENTS
    public VBox cardPatientInfos;
    public GridPane gridPaneEditForm;
    public Button btnModifyPatient;
    public TextField inputFirstnamePatient;
    public TextField inputLastnamePatient;
    public ComboBox<Genre> comboGenrePatient;
    public TextField inputBirthdayPatient;
    public TextField inputAddressPatient;
    public TextField inputPostalCodePatient;
    public AutoCompleteTextField autoCompleteCityPatient;
    public AutoCompleteTextField autoCompleteJobPatient;
    public TextField inputEmailPatient;
    public TextField inputMobilePhonePatient;
    public TextField inputFixPhonePatient;
    public TextField inputRelativePatient;
    public CheckBox checkBruxismePatient;
    public CheckBox checkOilAllergyPatient;
    public TextArea inputMedicalHistoryPatient;
    public TextArea inputContraindicationPatient;
    public TextArea inputCanceledAptmtPatient;
    public TextArea inputTreatmentPatient;
    public Label infoLabel;
    public Button btnIsEnglish;
    public ScrollPane scrollPane;
    public ImageView imgFlagEdit;
    public Label lblFirstnamePatient;
    public Label lblLastnamePatient;
    public Label lblBirthdayPatient;
    public Label lblGenrePatient;
    public VBox showPane;
    public VBox editPane;
    public Label lblAddressPatient;
    public Label lblCityPatient;
    public Label lblPostalCodePatient;
    public Label lblJobPatient;
    public Label lblTutorPatient;
    public Label lblEmailPatient;
    public Label lblMobilePatient;
    public Label lblFixPatient;
    public Label lblMedHistoryPatient;
    public Label lblImportedMedicalHistoryPatient;
    public Label lblContraindicationPatient;
    public Label lblTreatmentPatient;
    public Label lblCanceledApmtPatient;
    public CheckBox checkOilAllergyPatientLbl;
    public CheckBox checkBruxismePatientLbl;
    public Button lblIsEnglish;
    public VBox cardEditPane;
    public VBox cardShowPane;
    public VBox cardEditHealthPane;
    public VBox cardShowHealthPane;
    public GridPane gridPaneShowForm;
    public GridPane gridPaneEditHealthForm;
    public GridPane gridPaneShowHealthForm;
    public ImageView imgFlagShow;
    public ImageView imgCanceledApptmtEdit;
    public ImageView imgCanceledApptmtShow;
    public Label lblTitlePage;
    public HBox paneActionButtons;
    public Label lblTitleContraindicationPatient;
    public VBox osteoporoseConteneurEdit;
    public VBox osteoporoseConteneurShow;


    // SERVICES
    public PatientService patientService;
    public CityService cityService;
    public JobService jobService;
    public NavigationManager navigationManager;
    public EventManager eventManager;

    // LOCAL VARIABLES
    public String parentConteneur;
    public boolean isEnglish = false;
    public City patientCity;
    public Job patientJob;
    public List<City> cityList;
    public List<Job> jobList;
    public ApplicationContext applicationContext;
    public List<Node> editPaneItems;
    public List<Node> showPaneItems;
    public List<Node> healthEditPaneItems;
    public List<Node> healthShowPaneItems;
    public Patient localPatient;
    public OsteoporoseComponent osteoporoseComponentEdit = new OsteoporoseComponent(Osteoporose.AUCUN, true);
    public OsteoporoseComponent osteoporoseComponentShow = new OsteoporoseComponent(Osteoporose.AUCUN, false);
    public static final org.slf4j.Logger logger = LoggerFactory.getLogger(PatientInfosController.class);

    private static final PseudoClass ACTIVE_LANGUAGE = PseudoClass.getPseudoClass("activated");


    public PatientInfosController() {
    }

    // INITIALIZATION METHODS
    public void initialize(){
        initServices();

        initComponents();
    }

    public void initServices() {
        this.patientService = ServiceLocator.INSTANCE.getPatientService();
        this.cityService = ServiceLocator.INSTANCE.getCityService();
        this.jobService = ServiceLocator.INSTANCE.getJobService();
        this.navigationManager = NavigationManager.getInstance();
        this.applicationContext = ServiceLocator.INSTANCE.getApplicationContext();

        this.eventManager = EventManager.getInstance();
        eventManager.subscribe(PatientUpdatedEvent.class, event -> {
            // Update the local patient and reload infos
            this.localPatient = event.patient();
            fillLabelsPatientInfos(this.localPatient);
        });
    }

    public void initComponents(){
        Utils.insertIconInButton(btnModifyPatient, "/data/modify.png", Color.WHITE, ContentDisplay.RIGHT);

        Form.initInputFormatters(inputLastnamePatient, inputFirstnamePatient);
        Form.addListenerDateFormater(inputBirthdayPatient, null);
        this.cityList = this.cityService.getAll();
        Form.initAutoCompleteCity(autoCompleteCityPatient, inputPostalCodePatient, this.cityList, this.cityService);

        // Augment the scroll speed of the scroll pane
        Form.changeScrollSpeed(scrollPane, 0.5);

        this.jobList = this.jobService.getAll();
        autoCompleteJobPatient.getEntries().addAll(this.jobList.stream().map(Job::getName).toList());

        Genre[] genres = Genre.values();
        comboGenrePatient.getItems().addAll(genres);

        addCityFlagListener();

        // If patient has a canceled appointment, show the icon
        if(this.applicationContext.getCurrentPatient() != null && !this.applicationContext.getCurrentPatient().getCanceledAppointment().isEmpty()) {
            imgCanceledApptmtEdit.setVisible(true);
            imgCanceledApptmtEdit.setManaged(true);
            imgCanceledApptmtEdit.setImage(new Image("/data/canceled-apt.png"));
            Tooltip.install(imgCanceledApptmtEdit, new Tooltip(this.applicationContext.getCurrentPatient().getCanceledAppointment()));
            imgCanceledApptmtShow.setVisible(true);
            imgCanceledApptmtShow.setManaged(true);
            imgCanceledApptmtShow.setImage(new Image("/data/canceled-apt.png"));
            Tooltip.install(imgCanceledApptmtShow, new Tooltip(this.applicationContext.getCurrentPatient().getCanceledAppointment()));
        } else {
            imgCanceledApptmtEdit.setVisible(false);
            imgCanceledApptmtEdit.setManaged(false);
            imgCanceledApptmtShow.setVisible(false);
            imgCanceledApptmtShow.setManaged(false);
        }

        // Add a listener to all editable nodes to automatically save patient in db
        listenerInputNodes();
    }

    /**
     * Check if the form is valid, if not, display an error message
     * Check if at least firstname, lastname, birthday, address is filled
     * Check if email is valid
     * Check if phones are valid
     * @return true if the form is valid, false otherwise
     */
    public boolean verifyFormValidity(){
        // Get patient job or create it if necessary
        this.patientJob = jobService.getByName(autoCompleteJobPatient.getText());
        if(this.patientJob == null && !autoCompleteJobPatient.getText().isEmpty()) {
            try {
                this.patientJob = new Job(0, autoCompleteJobPatient.getText());
                jobService.save(patientJob);
                this.jobList.add(patientJob);
                autoCompleteJobPatient.getEntries().add(patientJob.getName());
            } catch (Exception e) {
                this.patientJob = null;
                e.printStackTrace();
            }
        }

        if(inputFirstnamePatient.getText().trim().isEmpty() || inputLastnamePatient.getText().trim().isEmpty() || inputBirthdayPatient.getText().trim().isEmpty()) {
            Logger.getInstance().showMessagePopUp("Veuillez remplir tous les champs obligatoires : nom, prénom et date de naissance.", MessageType.ERROR);
            return false;
        }

        // Check that city and postal code are filled and valid
        String patientCity = autoCompleteCityPatient.getText();
        String patientPostalCode = inputPostalCodePatient.getText().trim();
        if(!patientCity.isEmpty() || !patientPostalCode.isEmpty()) {
            City city = cityService.getByNamePostalCode(patientCity, patientPostalCode);
            if(city == null){
                // If city not found, create entity
                city = new City(0, patientCity, patientPostalCode, null);
                try {
                    this.cityService.save(city);
                } catch (Exception e) {
                    Logger.getInstance().showMessagePopUp("Erreur lors de la création de la ville, consulter les logs.", MessageType.ERROR);
                    logger.error("Erreur lors de la création de la ville sur la fiche patient : " + e.getMessage());
                    return false;
                }
            }
            this.patientCity = city;
        }

        if(inputEmailPatient.getText() != null && !inputEmailPatient.getText().isEmpty()) {
            String patientEmail = inputEmailPatient.getText().trim();
            if(!Form.isValidEmail(patientEmail)) {
                Logger.getInstance().showMessagePopUp("L'adresse mail saisie n'est pas valide.", MessageType.ERROR);
                return false;
            }
        }

        Logger.getInstance().showMessagePopUp("Données du patient valides.", MessageType.INFO);
        return true;
    }

    public void manageVisibilityAndDataPanes(boolean showLabelsItems, boolean fillItemsWithData, Patient patient){
        // If there is a current patient, show his infos in labels, else create empty object
        if(patient == null){
            Patient newPatient = new Patient();
            this.applicationContext.setCurrentPatient(newPatient);
        }

        // Labels
        if(showLabelsItems && fillItemsWithData) {
            fillLabelsPatientInfos(patient);
            this.showPaneItems = new ArrayList<>(gridPaneShowForm.getChildren());
            this.healthShowPaneItems = new ArrayList<>(gridPaneShowHealthForm.getChildren());
        // Inputs
        } else if (!showLabelsItems && fillItemsWithData) {
            fillInputsPatientInfos(patient);
            this.editPaneItems = new ArrayList<>(gridPaneEditForm.getChildren());
            this.healthEditPaneItems = new ArrayList<>(gridPaneEditHealthForm.getChildren());
        }

        if(showLabelsItems) {
            btnModifyPatient.setVisible(true);
            paneActionButtons.setVisible(false);
            editPane.setVisible(false);
            editPane.setManaged(false);
            showPane.setVisible(true);
            showPane.setManaged(true);

            // Create and insert osteoporose component in conteneur
            this.osteoporoseComponentShow.setOsteoporose(patient != null ? patient.getOsteoporose() : Osteoporose.AUCUN);
            this.osteoporoseComponentShow.setEditable(false);
            osteoporoseConteneurShow.getChildren().clear();
            osteoporoseConteneurShow.getChildren().add(this.osteoporoseComponentShow);
        } else {
            editPane.setVisible(true);
            editPane.setManaged(true);
            showPane.setVisible(false);
            showPane.setManaged(false);
            btnModifyPatient.setVisible(false);
            paneActionButtons.setVisible(true);

            // Create and insert osteoporose component in conteneur
            this.osteoporoseComponentEdit.setOsteoporose(patient != null ? patient.getOsteoporose() : Osteoporose.AUCUN);
            this.osteoporoseComponentEdit.setEditable(true);
            osteoporoseConteneurEdit.getChildren().clear();
            osteoporoseConteneurEdit.getChildren().add(this.osteoporoseComponentEdit);
        }
    }

    public void fillLabelsPatientInfos(Patient patient){
        // common infos
        City patientCity = patient.getCity();
        lblFirstnamePatient.setText(patient.getFirstname());
        lblLastnamePatient.setText(patient.getLastname());
        lblBirthdayPatient.setText(patient.getBirthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " (" + patient.getAgeString() + ")");
        lblGenrePatient.setText(patient.getGenre() != null ? patient.getGenre().toString() : "");
        lblAddressPatient.setText(patient.getAddress());
        lblCityPatient.setText(patientCity != null ? patientCity.getName() : "");
        lblPostalCodePatient.setText(patientCity != null ? patientCity.getPostalCode() : "");
        lblTutorPatient.setText(patient.getTutorRelative());
        lblJobPatient.setText(patient.getJob() != null ? patient.getJob().getName() : "");
        lblEmailPatient.setText(patient.getEmail());
        lblMobilePatient.setText(patient.getMobilePhone());
        lblFixPatient.setText(patient.getFixPhone());

        // health infos
        checkBruxismePatientLbl.setSelected(patient.isBruxisme());
        checkOilAllergyPatientLbl.setSelected(patient.isOilAllergy());
        lblMedHistoryPatient.setText(patient.getMedicalHistory());
        if(patient.getContraindication() != null && !patient.getContraindication().isEmpty()){
            lblTitleContraindicationPatient.getStyleClass().add("label-red");
            lblTitleContraindicationPatient.getStyleClass().remove("label-of-value");
        }
        lblContraindicationPatient.setText(patient.getContraindication());
        lblTreatmentPatient.setText(patient.getTreatment());
        lblCanceledApmtPatient.setText(patient.getCanceledAppointment());
        lblImportedMedicalHistoryPatient.setText(patient.getMedicalImportedHistory());

        if(patient.isEnglish()){
            lblIsEnglish.setVisible(true);
            lblIsEnglish.setManaged(true);
            lblIsEnglish.pseudoClassStateChanged(ACTIVE_LANGUAGE, true);
        }

        // flag of city
        if(patient.getCity() == null || patient.getCity().getDepartement() == null){
            return;
        }
        switch (patient.getCity().getDepartement().getId()) {
            case 102:
                imgFlagEdit.setImage(new Image("/data/kanaki.jpg"));
                imgFlagEdit.setVisible(true);
                imgFlagEdit.setManaged(true);
                imgFlagShow.setImage(new Image("/data/kanaki.jpg"));
                imgFlagShow.setVisible(true);
                imgFlagShow.setManaged(true);
                break;
            case 103:
                imgFlagEdit.setImage(new Image("/data/polynesie.jpg"));
                imgFlagEdit.setVisible(true);
                imgFlagEdit.setManaged(true);
                imgFlagShow.setImage(new Image("/data/polynesie.jpg"));
                imgFlagShow.setVisible(true);
                imgFlagShow.setManaged(true);
                break;
            default:
                imgFlagEdit.setVisible(false);
                imgFlagEdit.setManaged(false);
                imgFlagShow.setVisible(false);
                imgFlagShow.setManaged(false);
                break;

        }
    }

    public void fillInputsPatientInfos(Patient patient){
        // common infos
        City patientCity = patient.getCity();
        inputFirstnamePatient.setText(patient.getFirstname());
        inputLastnamePatient.setText(patient.getLastname());
        inputBirthdayPatient.setText(patient.getBirthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        comboGenrePatient.setValue(patient.getGenre());
        inputAddressPatient.setText(patient.getAddress());
        autoCompleteCityPatient.setText(patientCity != null ? patientCity.getName() : "");
        inputPostalCodePatient.setText(patientCity != null ? patientCity.getPostalCode() : "");
        inputRelativePatient.setText(patient.getTutorRelative());
        autoCompleteJobPatient.setText(patient.getJob() != null ? patient.getJob().getName() : "");
        inputEmailPatient.setText(patient.getEmail());
        inputMobilePhonePatient.setText(patient.getMobilePhone());
        inputFixPhonePatient.setText(patient.getFixPhone());

        autoCompleteCityPatient.hidePopup();
        autoCompleteJobPatient.hidePopup();

        // health infos
        checkBruxismePatient.setSelected(patient.isBruxisme());
        checkOilAllergyPatient.setSelected(patient.isOilAllergy());
        inputMedicalHistoryPatient.setText(patient.getMedicalHistory());
        inputContraindicationPatient.setText(patient.getContraindication());
        inputTreatmentPatient.setText(patient.getTreatment());
        inputCanceledAptmtPatient.setText(patient.getCanceledAppointment());

        if(patient.isEnglish()){
            btnIsEnglish.pseudoClassStateChanged(ACTIVE_LANGUAGE, true);
        }
    }


    public void addCityFlagListener(){
        autoCompleteCityPatient.focusedProperty().addListener((observable, oldValue, newValue) -> {
            City city = cityService.getByNamePostalCode(autoCompleteCityPatient.getText(), inputPostalCodePatient.getText());
            if(city == null || city.getDepartement() == null) return;

            switch (city.getDepartement().getId()) {
                case 102:
                    imgFlagEdit.setImage(new Image("/data/kanaki.jpg"));
                    imgFlagEdit.setVisible(true);
                    break;
                case 103:
                    imgFlagEdit.setImage(new Image("/data/polynesie.jpg"));
                    imgFlagEdit.setVisible(true);
                    break;
                default:
                    imgFlagEdit.setVisible(false);
                    break;

            }
        });
    }

    public void manageGridPaneColumns(GridPane gridPane, List<Node> paneItemsList, Parent paneCard, int nbColumnsMax){
        if(gridPane.getChildren().isEmpty()) return;

        int columns = nbColumnsMax;
        double gridWidth = gridPane.getWidth();
        if(gridWidth < 400) {
            columns = 1;
        } else if (gridWidth < 700) {
            columns = 2;
        } else if(gridPane.getWidth() < 800 && nbColumnsMax >= 3) {
            columns = 3;
        } else if(nbColumnsMax >= 4) {
            columns = 4;
        }

        rebuildGrid(gridPane, paneItemsList, paneCard, columns);
    }

    public void addGridPaneWidthListener(GridPane gridPane, List<Node> paneItemsList, Parent paneCard, int nbColumnsMax){
        if(gridPane.getChildren().isEmpty()) return;

        // Call to this method to rebuild the grid pane when the frame is initialized (because only the listener doesnt do the job)
        manageGridPaneColumns(gridPane, paneItemsList, paneCard, nbColumnsMax);

        gridPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            int columns = nbColumnsMax;

            if(newWidth.doubleValue() < 400) {
                columns = 1;
            } else if (newWidth.doubleValue() < 700) {
                columns = 2;
            } else if(newWidth.doubleValue() < 800 && nbColumnsMax >= 3) {
                columns = 3;
            } else if(nbColumnsMax >= 4) {
                columns = 4;
            }

            rebuildGrid(gridPane, paneItemsList, paneCard, columns);
        });
    }

    private void rebuildGrid(GridPane gridPane, List<Node> paneItemsList, Parent paneCard, int columns) {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();

        for (int i = 0; i < columns; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / columns);
            gridPane.getColumnConstraints().add(cc);
        }

        for (int i = 0; i < paneItemsList.size(); i++) {
            int col = i % columns;
            int row = i / columns;

            gridPane.add(paneItemsList.get(i), col, row);
        }
        // Request layout to let FX re calculate the width of component
        paneCard.requestLayout();
    }

    public void savePatientDatabase(){
        Consultation currentConsultation = this.applicationContext.getCurrentConsultation();
        Patient patient = this.applicationContext.getCurrentPatient();
        Patient currentPatient = patient == null ? currentConsultation.getPatient() : patient;

        // Create patient object with datas
        String patientFirstname = inputFirstnamePatient.getText();
        String patientLastname = inputLastnamePatient.getText();
        LocalDate patientBirthday = LocalDate.parse(inputBirthdayPatient.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Genre patientGenre = comboGenrePatient.getValue();
        String patientAddress = inputAddressPatient.getText();
        String patientEmail = inputEmailPatient.getText();
        String patientMobilePhone = inputMobilePhonePatient.getText();
        String patientFixPhone = inputFixPhonePatient.getText();
        String patientRelative = inputRelativePatient.getText();
        boolean patientEnglish = isEnglish;
        boolean patientBruxisme = checkBruxismePatient.isSelected();
        Osteoporose patientOsteoporose = this.osteoporoseComponentEdit.getOsteoporose();
        boolean patientOilAllergy = checkOilAllergyPatient.isSelected();
        String patientMedicalHistory = inputMedicalHistoryPatient.getText();
        String patientImportedMedicalHistory = currentPatient != null ? currentPatient.getMedicalImportedHistory() : "";
        String patientContraindication = inputContraindicationPatient.getText();
        String patientTreatment = inputTreatmentPatient.getText();
        String patientCanceledAptmt = inputCanceledAptmtPatient.getText();

        Patient patientEntity = new Patient(currentPatient != null ? currentPatient.getId() : 0, patientLastname, patientFirstname, patientBirthday, patientGenre, patientAddress,
                patientEmail, patientMobilePhone, patientFixPhone, patientRelative, patientEnglish, patientBruxisme, patientOsteoporose,
                patientOilAllergy, patientMedicalHistory, patientImportedMedicalHistory, patientContraindication, patientTreatment, patientCanceledAptmt, null,
                null, null, patientJob, patientCity);
        try {
            patientService.save(patientEntity);
            eventManager.publish(new PatientUpdatedEvent(patientEntity));
            this.applicationContext.setCurrentPatient(patientEntity);
            Logger.getInstance().showMessagePopUp("Patient sauvegardé avec succès.", MessageType.SUCCESS);
        } catch (Exception e) {
            Logger.getInstance().showMessagePopUp("Erreur lors de la sauvegarde du patient, consulter les logs.", MessageType.ERROR);
            e.printStackTrace();
        }
    }

    public void changeActionFormMode(ActionOnOpen actionOnOpen){
        Patient patient = this.localPatient != null ? this.localPatient : this.applicationContext.getCurrentPatient();
        switch (actionOnOpen) {
            case SHOW:
                manageVisibilityAndDataPanes(true, true, patient);
                addGridPaneWidthListener(gridPaneShowForm, showPaneItems, cardShowPane, 4);
                addGridPaneWidthListener(gridPaneShowHealthForm, healthShowPaneItems, cardShowHealthPane, 2);

                lblTitlePage.setText("Fiche patient");
                break;
            case EDIT:
                manageVisibilityAndDataPanes(false, true, patient);
                addGridPaneWidthListener(gridPaneEditForm, editPaneItems, cardEditPane, 4);
                addGridPaneWidthListener(gridPaneEditHealthForm, healthEditPaneItems, cardEditHealthPane, 2);
                paneActionButtons.setVisible(true);
                lblTitlePage.setText("Modifier fiche patient");
                break;
            case ADD:
                manageVisibilityAndDataPanes(false, false, null);
                paneActionButtons.setVisible(true);
                lblTitlePage.setText("Nouvelle fiche patient");
                break;
        }
    }

    // OVERRIDE/IMPLEMENTS METHODS

    @Override
    public void onOpen(ActionOnOpen actionOnOpen) {
        changeActionFormMode(actionOnOpen);
    }


    @Override
    public void onOpen(Object entity) {
        this.localPatient = (Patient) entity;
    }

    @Override
    public void onClose() {
        this.localPatient = null;
    }


    @Override
    public void setParentConteneur(String parentConteneur) {
        this.parentConteneur = parentConteneur;
    }

    // LISTENER BUTTONS ACTIONS (HANDLE)

    public void handleSavePatient(ActionEvent actionEvent) {
        if(!verifyFormValidity()) {
            return;
        }

        savePatientDatabase();

        changeActionFormMode(ActionOnOpen.SHOW);
    }

    public void handleUpdatePatient(ActionEvent actionEvent) {
        changeActionFormMode(ActionOnOpen.EDIT);
    }

    /**
     * Event listener of english button, when clicked it sets the attribute isEnglish to true, or false
     * And modify the background image of button accordingly
     */
    public void handleClickEnglishBtn() {
        // change pseudo class of button depending on current state
        this.isEnglish = !this.isEnglish;
        btnIsEnglish.pseudoClassStateChanged(ACTIVE_LANGUAGE, this.isEnglish);
    }

    public void handleCancelModifyPatient(ActionEvent actionEvent) {
        // if current patient is null, go back to home page (it means controller is in new patient mode)
        if(this.applicationContext.getCurrentPatient() == null) {
            this.navigationManager.back(this.parentConteneur);
            return;
        }
        changeActionFormMode(ActionOnOpen.SHOW);
    }

    public void listenerInputNodes() {
        forEachNodeOfType(cardPatientInfos, TextField.class, textField -> {
            textField.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if(!newValue) {
                    inputChanged();
                }
            });
        });

        forEachNodeOfType(cardPatientInfos, TextArea.class, textArea -> {
            textArea.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if(!newValue) {
                    inputChanged();
                }
            });
        });

        forEachNodeOfType(cardPatientInfos, ComboBox.class, comboBox -> {
            comboBox.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if(!newValue) {
                    inputChanged();
                }
            });
        });

        forEachNodeOfType(cardPatientInfos, CheckBox.class, checkBox -> {
            checkBox.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if(!newValue) {
                    inputChanged();
                }
            });
        });
    }

    public void inputChanged() {
        if(verifyFormValidity()){
            savePatientDatabase();
        }
    }
}
