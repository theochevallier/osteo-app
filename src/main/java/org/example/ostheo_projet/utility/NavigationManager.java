package org.example.ostheo_projet.utility;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.ostheo_projet.Interface.Navigable;
import org.example.ostheo_projet.enums.ActionOnOpen;
import org.example.ostheo_projet.model.ConsultationFilters;
import org.example.ostheo_projet.model.PatientFilters;
import org.example.ostheo_projet.service.ServiceLocator;
import org.example.ostheo_projet.ui.controller.ConsultationTableController;
import org.example.ostheo_projet.ui.controller.PatientTableController;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public final class NavigationManager {
    private static NavigationManager instance;
    private ApplicationContext applicationContext;
    private Stage stage;
    private final Map<String, Stack<String>> navigationHistories = new HashMap<>();
    private final Map<String, Stack<String>> titleHistories = new HashMap<>();
    private final Map<String, Navigable> controllers = new HashMap<>();
    private final Map<String, Pane> paneConteneurs = new HashMap<>();
    private final Map<String, BooleanProperty> paneCanNavigateBackMap = new HashMap<>();

    /**
     * Private constructor to prevent direct instantiation.
     * Use getInstance() to get the singleton instance.
     */
    private NavigationManager() {
        this.applicationContext = ServiceLocator.INSTANCE.getApplicationContext();
    }

    /**
     * Gets the singleton instance of NavigationManager.
     * Creates a new instance if one doesn't exist.
     *
     * @return the singleton NavigationManager instance
     */
    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    /**
     * Sets the primary stage for scene-based navigation.
     *
     * @param stage the primary stage to use for navigation
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Gets the primary stage used for scene-based navigation.
     *
     * @return the primary stage, or null if not set
     */
    public Stage getStage() {
        return this.stage;
    }

    public BooleanProperty getCanPaneNavigateBack(String paneId){
        return paneCanNavigateBackMap.get(paneId);
    }

    public void addPaneConteneur(String paneId, Pane pane){
        paneConteneurs.put(paneId, pane);
        paneCanNavigateBackMap.put(paneId, new SimpleBooleanProperty(false));
    }

    /**
     * Loads an FXML component into the target pane instead of replacing the entire scene.
     * This allows keeping other UI elements (like TabPane) visible while changing content.
     *
     * @param fxmlName the name of the FXML file to load
     * @param actionOnOpen the action string
     */
    private void loadComponent(String fxmlName, ActionOnOpen actionOnOpen, String paneId, Object entity) {
        Stack<String> navigationHistory = navigationHistories.get(paneId);

        Pane targetPane;
        if (paneId != null) {
            targetPane = paneConteneurs.get(paneId);
        } else {
            targetPane = null;
        }
        if (targetPane == null) {
            System.err.println("Target pane not found, it must be set before loading components in it.");
            return;
        }

        Object parentController = this.controllers.get(navigationHistory.size() > 1 ? navigationHistory.get(navigationHistory.size() - 2) : null);
        Parent componentRoot = Window.loadFXML(fxmlName, parentController, stage);

        if (componentRoot != null) {
            Navigable controller = (Navigable) componentRoot.getUserData();

            if(controller instanceof PatientTableController){
                PatientFilters patientFilters = this.applicationContext.getPatientFilters();
                ((PatientTableController) controller).fillInputsFiltersAndFilterTable(patientFilters);
            }

            if(controller instanceof ConsultationTableController){
                ConsultationFilters consultationFilters = this.applicationContext.getConsultationFilters();
                ((ConsultationTableController) controller).fillConsultationTableFilters(consultationFilters);
            }

            controllers.put(fxmlName, controller);

            // Clear the target pane and add the new component
            targetPane.getChildren().clear();
            targetPane.getChildren().add(componentRoot);

            Platform.runLater(() -> {
                if(controller != null){
                    if(entity != null){
                        controller.onOpen(entity);
                    }
                    controller.onOpen(actionOnOpen);
                    controller.setParentConteneur(paneId);
                }
            });
        }
    }

    /**
     * Navigates to a new component by loading it into the target pane.
     * This allows keeping other UI elements (like TabPane) visible while changing content.
     * The target pane must be set using setTargetPane() before calling this method.
     *
     * @param fxmlName the name of the FXML file to load as a component
     * @param actionOnOpen the action to perform on open
     * @param title the title for the component (for consistency, not currently used)
     */
    public void navigateToComponent(String fxmlName, ActionOnOpen actionOnOpen, String title, String paneId, boolean isPaneNavigable, Object entity) {
        Stack<String> paneHistory = navigationHistories.computeIfAbsent(paneId, k -> new Stack<>());
        Stack<String> paneTitleHistory = titleHistories.computeIfAbsent(paneId, k -> new Stack<>());

        paneHistory.push(fxmlName);
        paneTitleHistory.push(title);

        paneCanNavigateBackMap.get(paneId).set(isPaneNavigable);

        loadComponent(fxmlName, actionOnOpen, paneId, entity);
    }

    public void navigateToComponent(String fxmlName, ActionOnOpen actionOnOpen, String title, String paneId, boolean isPaneNavigable) {
        navigateToComponent(fxmlName, actionOnOpen, title, paneId, isPaneNavigable, null);
    }

    public Object getControllerByFxmlName(String fxmlName) {
        return controllers.get(fxmlName);
    }

    /**
     * Navigates back to the previous view in the navigation history.
     * If a target pane is set, it will load the previous view as a component.
     * Otherwise, it will load the previous view as a full scene.
     */
    public void back(String paneId) {
        Stack<String> navigationHistory = this.navigationHistories.get(paneId);
        Stack<String> titleHistory = this.titleHistories.get(paneId);

        if (navigationHistory != null && navigationHistory.size() > 1) {
            navigationHistory.pop();
            titleHistory.pop();
            String previousFxml = navigationHistory.peek();
            String title = titleHistory.peek();

            loadComponent(previousFxml, ActionOnOpen.NONE, paneId, null);
        }

        assert navigationHistory != null;
        paneCanNavigateBackMap.get(paneId).set(navigationHistory.size() > 1);
    }
}
