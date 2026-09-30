package org.example.ostheo_projet.utility;

import javafx.fxml.FXMLLoader;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.example.ostheo_projet.RootApplication;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class that helps render new windows by injecting services in it. the method createOrShowWindow prevents from creating same window
 * multiple times
 */
public class Window {

    private static final String viewsPath = "/org/example/ostheo_projet/ui/view/";
    private static final Map<String, Stage> activeWindows = new HashMap<>();
    private static Parent root;
    private static Object controller;

    public static Stage createWindow(Object parentController, String title, String fxmlPath, boolean resizable, int width, int height, boolean maximize) {
        Stage newStage = new Stage();
        loadFXMLAndServiceInjection(fxmlPath, parentController, newStage);

        Scene scene = new Scene(root, width, height);
        scene.getRoot().setUserData(controller);
        scene.getStylesheets().add(RootApplication.class.getResource("/style.css").toExternalForm());
        scene.getStylesheets().add(RootApplication.class.getResource("/label.css").toExternalForm());
        newStage.setTitle(title);
        newStage.setScene(scene);
        newStage.setResizable(resizable);

        // Maximize window to max screen
        newStage.setMaximized(maximize);
        if(maximize) {
            // Second windows dont always take max size, so we set width and height manually
            newStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
            newStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
        }
        newStage.show();
        newStage.toFront();
        newStage.requestFocus();
        return newStage;
    }

    public static Stage createOrShowWindow(String windowId, Object parentController, String title,
                                           String fxmlPath, boolean resizable, int width, int height, boolean maximize) {
        // Si fenêtre existe et est ouverte
        if (activeWindows.containsKey(windowId)) {
            Stage stage = activeWindows.get(windowId);
            if (stage.isShowing()) {
                stage.toFront();
                stage.requestFocus();
                return stage;
            }
        }

        // Sinon créer nouvelle fenêtre
        Stage stage = createWindow(parentController, title, fxmlPath, resizable, width, height, maximize);

        // When closing the window, remove from this list
        if(stage != null){
            stage.setOnCloseRequest(e -> activeWindows.remove(windowId));
            activeWindows.put(windowId, stage);
            return stage;
        }
        return null;
    }

    public static Parent loadFXML(String fxmlPath, Object parentController, Stage currentStage) {
        loadFXMLAndServiceInjection(fxmlPath, parentController, currentStage);
        root.setUserData(controller);
        return root;
    }

    public static void loadFXMLAndServiceInjection(String fxmlPath, Object parentController, Stage currentStage) {
        try {
            URL url = RootApplication.class.getResource(viewsPath + fxmlPath);
            FXMLLoader loader = new FXMLLoader(url);
            root = loader.load();
            controller = loader.getController();

            //Services injection
            // TODO : factoriser dans une classe

            NavigationManager navigationManager = NavigationManager.getInstance();
            if(NavigationManager.getInstance().getStage() == null){
                navigationManager.setStage(currentStage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeWindow(String windowId) {
        Stage stage = activeWindows.get(windowId);
        if (stage != null) {
            stage.hide();
        }
        activeWindows.remove(windowId);
    }

    public static void closeAllWindows() {
        for(Map.Entry<String, Stage> entry : activeWindows.entrySet()){
            closeWindow(entry.getKey());
        }
        activeWindows.clear();
    }

    public static void addWindow(String windowId, Stage stage) {
        activeWindows.put(windowId, stage);
    }

    public static Stage getWindow(String windowId) {
        return activeWindows.get(windowId);
    }

    public static Map<String, Stage> getActiveWindows() {
        return activeWindows;
    }
}
