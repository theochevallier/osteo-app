package org.example.ostheo_projet.ui.component;


import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.example.ostheo_projet.enums.ActionOnOpen;
import org.example.ostheo_projet.enums.DirectoryType;
import org.example.ostheo_projet.enums.MessageType;
import org.example.ostheo_projet.event.ConsultationUpdatedEvent;
import org.example.ostheo_projet.event.PatientUpdatedEvent;
import org.example.ostheo_projet.model.Configuration;
import org.example.ostheo_projet.model.Consultation;
import org.example.ostheo_projet.model.Patient;
import org.example.ostheo_projet.model.Practitioner;
import org.example.ostheo_projet.service.ConfigurationService;
import org.example.ostheo_projet.service.ConsultationService;
import org.example.ostheo_projet.service.ServiceLocator;
import org.example.ostheo_projet.utility.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.example.ostheo_projet.utility.Form.forEachNodeOfType;

public class ConsultationInfosComponent extends VBox {
    // FXML ELEMENTS
    @FXML public VBox cardConsultationInfos;
    @FXML public Button btnModifyConsult;
    @FXML public Button btnOpenFolder;
    @FXML public Label lblTitlePage;
    @FXML public FlowPane conteneurSelectedFilesEdit;
    @FXML public FlowPane conteneurSelectedFilesShow;
    @FXML public HBox paneActionButtons;
    @FXML public VBox paneEdit;
    @FXML public VBox paneShow;
    @FXML public Separator separatorEndFile;

    @FXML public TextField inputDateConsult;
    @FXML public TextArea inputReasonConsult;
    @FXML public TextArea inputTreatmentConsult;
    @FXML public TextArea inputMedicalExamConsult;
    @FXML public TextArea inputExclusionTestConsult;
    @FXML public TextArea inputFoodSuppConsult;
    @FXML public TextArea inputGivenAdvicesConsult;
    @FXML public TextArea inputGivenExercisesConsult;
    @FXML public CheckBox checkDoctorRelocEdit;
    @FXML public CheckBox checkDentistRelocEdit;
    @FXML public CheckBox checkOrthophonistRelocEdit;
    @FXML public CheckBox checkOrthodontistRelocEdit;
    @FXML public CheckBox checkOrthoptistRelocEdit;
    @FXML public CheckBox checkPodologueRelocEdit;
    @FXML public CheckBox checkIBCLCRelocEdit;
    @FXML public TextArea inputRelocationConsult;
    @FXML public TextArea inputNotesConsult;

    @FXML public Label lblDateConsult;
    @FXML public Label lblReasonConsult;
    @FXML public Label lblTreatmentConsult;
    @FXML public Label lblMedicalExamConsult;
    @FXML public Label lblExclusionTestConsult;
    @FXML public Label lblFoodSuppConsult;
    @FXML public Label lblGivenAdvicesConsult;
    @FXML public Label lblGivenExercisesConsult;
    @FXML public CheckBox checkDoctorRelocShow;
    @FXML public CheckBox checkDentistRelocShow;
    @FXML public CheckBox checkOrthophonistRelocShow;
    @FXML public CheckBox checkOrthodontistRelocShow;
    @FXML public CheckBox checkOrthoptistRelocShow;
    @FXML public CheckBox checkPodologueRelocShow;
    @FXML public CheckBox checkIBCLCRelocShow;
    @FXML public Label lblRelocationConsult;
    @FXML public Label lblNotesConsult;

    // SERVICES
    public ApplicationContext applicationContext;
    public ConsultationService consultationService;
    public NavigationManager navigationManager;
    public ConfigurationService configurationService;
    public EventManager eventManager;

    // LOCAL VARIABLES
    public String parentConteneur;
    private final ObservableList<File> selectedFiles = FXCollections.observableArrayList();
    private Consultation consultation;

    public ConsultationInfosComponent(Consultation consultation) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/ostheo_projet/ui/view/consultation-infos-component.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        this.applicationContext = ServiceLocator.INSTANCE.getApplicationContext();
        this.eventManager = EventManager.getInstance();

        if(consultation != null) {
            this.consultation = consultation;
        } else {
            Patient patient = this.applicationContext.getCurrentPatient();
            Practitioner practitioner = this.applicationContext.getCurrentPractitioner();
            if(practitioner != null && patient != null) {
                this.consultation = new Consultation(patient, practitioner);
            } else {
                return;
            }
        }

        try {
            loader.load();

            initializeComponents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setConsultation(Consultation consultation){
        this.consultation = consultation;
    }

    public Consultation getConsultation() {
        return this.consultation;
    }

    // INITIALIZATION METHODS
    public void initializeComponents() {
        initServices();
        initForm();
        listenerInputNodes();
    }

    public void initServices(){
        this.applicationContext = ServiceLocator.INSTANCE.getApplicationContext();
        this.consultationService = ServiceLocator.INSTANCE.getConsultationService();
        this.navigationManager = NavigationManager.getInstance();
        this.configurationService = ServiceLocator.INSTANCE.getConfigurationService();
    }

    public void initForm(){
        Form.enableTabNextNode(inputReasonConsult, inputTreatmentConsult);
        Form.enableTabNextNode(inputTreatmentConsult, inputMedicalExamConsult);
        Form.enableTabNextNode(inputMedicalExamConsult, inputExclusionTestConsult);
        Form.enableTabNextNode(inputExclusionTestConsult, inputFoodSuppConsult);
        Form.enableTabNextNode(inputFoodSuppConsult, inputGivenAdvicesConsult);
        Form.enableTabNextNode(inputGivenAdvicesConsult, inputGivenExercisesConsult);
        Form.enableTabNextNode(inputGivenExercisesConsult, inputNotesConsult);

        // For time textfield, only allow two numbers : two numbers for time field
        Form.addListenerDateFormater(inputDateConsult, null);

        // Selected files component listener, if a file is added or removed the file component is created
        selectedFiles.addListener((ListChangeListener<? super File>) change -> {
            while (change.next()){
                if(change.wasAdded() || change.wasRemoved()){
                    fillSelectedFilesComponent(conteneurSelectedFilesEdit, true);
                }
            }
        });

        // icons insertion
        Utils.insertIconInButton(btnOpenFolder, "/data/folder.png", Color.WHITE, ContentDisplay.RIGHT);
        Utils.insertIconInButton(btnModifyConsult, "/data/modify.png", Color.WHITE, ContentDisplay.RIGHT);
    }


        // OTHER METHODS

    public void fillLabelsConsultInfos(){
        if(this.consultation == null) return;

        lblDateConsult.setText(consultation.getConsultationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblReasonConsult.setText(consultation.getReason());
        lblTreatmentConsult.setText(consultation.getTreatment());
        lblMedicalExamConsult.setText(consultation.getMedicalExam());
        lblExclusionTestConsult.setText(consultation.getExclusionTest());
        lblFoodSuppConsult.setText(consultation.getFoodSupplement());
        lblGivenAdvicesConsult.setText(consultation.getGivenAdvice());
        lblGivenExercisesConsult.setText(consultation.getGivenExercise());
        lblNotesConsult.setText(consultation.getNote());

        checkDoctorRelocShow.setSelected(consultation.isDoctorRelocation());
        checkDentistRelocShow.setSelected(consultation.isDentistRelocation());
        checkOrthoptistRelocShow.setSelected(consultation.isOrthoptisteRelocation());
        checkOrthophonistRelocShow.setSelected(consultation.isOrthophonisteRelocation());
        checkOrthodontistRelocShow.setSelected(consultation.isOrthodontisteRelocation());
        checkPodologueRelocShow.setSelected(consultation.isPodologueRelocation());
        checkIBCLCRelocShow.setSelected(consultation.isIbclcRelocation());
        lblRelocationConsult.setText(consultation.getRelocationNote());
    }

    public void fillInputsConsultInfos(){
        if(this.consultation == null) return;

        inputDateConsult.setText(consultation.getConsultationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        inputReasonConsult.setText(consultation.getReason());
        inputTreatmentConsult.setText(consultation.getTreatment());
        inputMedicalExamConsult.setText(consultation.getMedicalExam());
        inputExclusionTestConsult.setText(consultation.getExclusionTest());
        inputFoodSuppConsult.setText(consultation.getFoodSupplement());
        inputGivenAdvicesConsult.setText(consultation.getGivenAdvice());
        inputGivenExercisesConsult.setText(consultation.getGivenExercise());
        inputNotesConsult.setText(consultation.getNote());

        checkDoctorRelocEdit.setSelected(consultation.isDoctorRelocation());
        checkDentistRelocEdit.setSelected(consultation.isDentistRelocation());
        checkOrthoptistRelocEdit.setSelected(consultation.isOrthoptisteRelocation());
        checkOrthophonistRelocEdit.setSelected(consultation.isOrthophonisteRelocation());
        checkOrthodontistRelocEdit.setSelected(consultation.isOrthodontisteRelocation());
        checkPodologueRelocEdit.setSelected(consultation.isPodologueRelocation());
        checkIBCLCRelocEdit.setSelected(consultation.isIbclcRelocation());
        inputRelocationConsult.setText(consultation.getRelocationNote());
    }

    public void changeActionFormMode(ActionOnOpen actionOnOpen){

        switch (actionOnOpen) {
            case SHOW:
                manageVisibilityAndDataPanes(true, true);
                int consultationNumber = consultation.getConsultationNumber() != 0 ? consultation.getConsultationNumber() : this.consultationService.getCountByPatient(consultation.getPatient());
                lblTitlePage.setText("Consultation n°" + consultationNumber + " du " + this.consultation.getConsultationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                break;
            case EDIT:
                manageVisibilityAndDataPanes(false, true);
//
                paneActionButtons.setVisible(true);
                lblTitlePage.setText("Modifier consultation patient");
                break;
            case ADD:
                manageVisibilityAndDataPanes(false, false);
                LocalDate dateNow = LocalDate.now();
                inputDateConsult.setText(dateNow.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                paneActionButtons.setVisible(true);
                lblTitlePage.setText("Nouvelle consultation patient");
                break;
        }
    }

    public void manageVisibilityAndDataPanes(boolean showLabelsItems, boolean fillItemsWithData){
        // If there is a current patient, show his infos in labels
        // Labels SHOW
        if(showLabelsItems && fillItemsWithData) {
            fillLabelsConsultInfos();
            if(consultation != null){
                fillSelectedFilesConsultList(consultation, conteneurSelectedFilesShow, false);
            }

            // Inputs EDIT
        } else if (!showLabelsItems && fillItemsWithData) {
            fillInputsConsultInfos();
            if(consultation != null){
                fillSelectedFilesConsultList(consultation, conteneurSelectedFilesEdit, true);
            }
        }

        if(showLabelsItems) {
            btnModifyConsult.setVisible(true);
            paneActionButtons.setVisible(false);
            paneEdit.setVisible(false);
            paneEdit.setManaged(false);
            paneShow.setVisible(true);
            paneShow.setManaged(true);
        } else {
            btnModifyConsult.setVisible(false);
            paneActionButtons.setVisible(true);
            paneEdit.setVisible(true);
            paneEdit.setManaged(true);
            paneShow.setVisible(false);
            paneShow.setManaged(false);
        }
    }

    public void fillSelectedFilesComponent(FlowPane conteneur, boolean showDeleteButton){
        conteneur.getChildren().clear();
        selectedFiles.forEach(file -> {
            SelectedFileComponent selectedFileComponent = new SelectedFileComponent(this.selectedFiles, showDeleteButton);
            selectedFileComponent.setSelectedFile(file);
            conteneur.getChildren().add(selectedFileComponent);
        });
    }

    public void fillSelectedFilesConsultList(Consultation consultation, FlowPane conteneur, boolean showDeleteButton) {
        this.selectedFiles.clear();
        this.selectedFiles.addAll(getConsultFilesList(consultation));
        fillSelectedFilesComponent(conteneur, showDeleteButton);
    }

    public List<File> getConsultFilesList(Consultation consultation) {
        String consultPath = Utils.getPatientsFilesDirectory() + consultation.getPatient().getId() + "/" + consultation.getId() + "/" + DirectoryType.CONSULTATION.toString().toLowerCase();
        File consultDirectory = new File(consultPath);
        return Utils.getFilesOfDirectory(consultDirectory);
    }

    public Consultation saveConsultationDatabase(){
        Patient patientConsult = this.consultation != null ? this.consultation.getPatient() : this.applicationContext.getCurrentPatient();
        if(patientConsult == null){
            Logger.getInstance().showMessagePopUp("Une erreur est survenue lors de la récupération du patient, veuillez sélectionner le patient à nouveau et réessayer.", MessageType.ERROR);
            return null;
        }

        Practitioner practitionerConsult = this.applicationContext.getCurrentPractitioner();
        if(practitionerConsult == null){
            Logger.getInstance().showMessagePopUp("Une erreur est survenue lors de la récupération du practicien, veuillez vous reconnecter et réessayer.", MessageType.ERROR);
            return null;
        }

        LocalDate dateConsult;
        try {
            dateConsult = LocalDate.parse(inputDateConsult.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            Logger.getInstance().showMessagePopUp("Le format de la date de consultation n'est pas valide.", MessageType.ERROR);
            return null;
        }

        String reasonConsult= inputReasonConsult.getText();
        String treatmentConsult = inputTreatmentConsult.getText();
        String medicalExamConsult = inputMedicalExamConsult.getText();
        String exclusionTestConsult = inputExclusionTestConsult.getText();
        String foodSupplementConsult = inputFoodSuppConsult.getText();
        String givenAdviceConsult = inputGivenAdvicesConsult.getText();
        String givenExerciseConsult = inputGivenExercisesConsult.getText();
        String personalNoteConsult = inputNotesConsult.getText();

        boolean isDoctorReloc = checkDoctorRelocEdit.isSelected();
        boolean isDentistReloc = checkDentistRelocEdit.isSelected();
        boolean isOrthophonistReloc = checkOrthophonistRelocEdit.isSelected();
        boolean isOrthodontistReloc = checkOrthodontistRelocEdit.isSelected();
        boolean isOrthoptistReloc = checkOrthoptistRelocEdit.isSelected();
        boolean isPodologueReloc = checkPodologueRelocEdit.isSelected();
        boolean isIBCLCReloc = checkIBCLCRelocEdit.isSelected();
        String relocationNote = inputRelocationConsult.getText();

        try {
            Consultation consultationToSave = new Consultation(this.consultation != null ? this.consultation.getId() : 0, dateConsult, reasonConsult, treatmentConsult, medicalExamConsult,
                    exclusionTestConsult, personalNoteConsult, givenAdviceConsult, givenExerciseConsult, foodSupplementConsult, isDoctorReloc, isDentistReloc, isOrthophonistReloc,
                    isOrthodontistReloc, isOrthoptistReloc, isPodologueReloc, isIBCLCReloc, relocationNote, LocalDateTime.now(), null, patientConsult, practitionerConsult);
            this.consultationService.save(consultationToSave);
            int countConsultations = this.consultationService.getCountByPatient(patientConsult);
            consultationToSave.setConsultationNumber(countConsultations); //this.consultation != null ? this.consultation.getConsultationNumber() :
            this.consultation = consultationToSave;
            eventManager.publish(new ConsultationUpdatedEvent(this.consultation));
            this.applicationContext.setCurrentConsultation(this.consultation);
            Logger.getInstance().showMessagePopUp("Consultation enregistrée avec succès.", MessageType.SUCCESS);
            return consultationToSave;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveFilesOfConsultation(Consultation consult) {
        if(consult != null){
            // Create patient consult directory if not exists
            String consultRootPath = Utils.createPatientConsultDirectoryIfNotExists(consult.getPatient().getId(), consult.getId());

            // Save files to directory
            for(File file : selectedFiles){
                String consultPath = consultRootPath + DirectoryType.CONSULTATION.toString().toLowerCase();


                // Verify that file dont already exists
                if(new File(consultPath + "/" + file.getName()).exists()){
                    continue;
                }
                Utils.copyFileToDirectory(consultPath, file);
            }

            // Remove files in case of user deleted a file in edit mode
            // The normalizeFileName method is used to compare file names (in case of accents, space etc)
            List<String> selectedFilesNames = selectedFiles.stream().map(file -> Utils.normalizeFileName(file.getName())).toList();
            getConsultFilesList(consult).forEach(file -> {
                if(!selectedFilesNames.contains(Utils.normalizeFileName(file.getName()))){
                    file.delete();
                }
            });

            // Reload data
            changeActionFormMode(ActionOnOpen.SHOW);
        }
    }


    // OVERRIDE/IMPLEMENTS METHODS

//    @Override
    public void onOpen(ActionOnOpen actionOnOpen) {
        changeActionFormMode(actionOnOpen);

        // Create consult directories if it doesnt exist
        saveFilesOfConsultation(this.consultation);
    }

    // LISTENER BUTTONS ACTIONS (HANDLE)

    @FXML
    public void handleUpdateConsult(ActionEvent actionEvent) {
        changeActionFormMode(ActionOnOpen.EDIT);
    }

    @FXML
    public void handleSaveConsult(ActionEvent actionEvent) {
        Consultation consult = saveConsultationDatabase();

        // Save files of consult to patient consult directory
        saveFilesOfConsultation(consult);
    }

    @FXML
    public void handleCancelModifyConsult(ActionEvent actionEvent) {
        if(this.consultation == null){
            this.navigationManager.back(this.parentConteneur);
        }
        changeActionFormMode(ActionOnOpen.SHOW);
    }


    @FXML
    public void handleChooseFile(ActionEvent actionEvent) {
        // Get configuration of practitioner for the default path file chooser
        Configuration config = this.configurationService.getByPractitioner(this.applicationContext.getCurrentPractitioner());
        String defaultPath = config.getDefaultConsultFilePath() != null && !config.getDefaultConsultFilePath().isEmpty() ? config.getDefaultConsultFilePath() : System.getProperty("user.home") + "/Desktop";
        List<File> selectedFiles = Utils.openFileChooser("Choisir un fichier", new FileChooser.ExtensionFilter("Tous les fichiers", "*.*"), defaultPath, false, true);
        if(selectedFiles != null){
            this.selectedFiles.addAll(selectedFiles);
        }
    }

    public void listenerInputNodes() {

        forEachNodeOfType(paneEdit, TextField.class, textField -> {
            textField.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if(!newValue) {
                    inputChanged();
                }
            });
        });

        forEachNodeOfType(paneEdit, TextArea.class, textArea -> {
            textArea.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if(!newValue) {
                    inputChanged();
                }
            });
        });

        forEachNodeOfType(paneEdit, ComboBox.class, comboBox -> {
            comboBox.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if(!newValue) {
                    inputChanged();
                }
            });
        });

        forEachNodeOfType(paneEdit, CheckBox.class, checkBox -> {
            checkBox.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if(!newValue) {
                    inputChanged();
                }
            });
        });
    }

    public void inputChanged() {
        Consultation consult = saveConsultationDatabase();
        saveFilesOfConsultation(consult);
    }
}
