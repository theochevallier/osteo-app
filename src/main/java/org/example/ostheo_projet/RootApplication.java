package org.example.ostheo_projet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.ostheo_projet.service.PractitionerService;
import org.example.ostheo_projet.service.ServiceLocator;
import org.example.ostheo_projet.ui.controller.ConnexionController;
import org.example.ostheo_projet.ui.controller.PractitionerInitController;
import org.example.ostheo_projet.utility.*;
import org.example.ostheo_projet.utility.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.desktop.SystemSleepEvent;
import java.awt.desktop.SystemSleepListener;
import java.io.IOException;

public class RootApplication extends Application {

    private static ApplicationContext applicationContext;
    private PractitionerService practitionerService;
    public static final Logger logger = LoggerFactory.getLogger(RootApplication.class);

    public static void main(String[] args) {
        // Initialize application context
        ApplicationInitializer.initDatabase();
        logger.info("Application started");

        // Permet d'afficher la barre de menu en haut de l'écran sur MacOS
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        applicationContext = ServiceLocator.INSTANCE.getApplicationContext();
        launch();
    }

    //Le stage est le conteneur principal de l'application (fenetre principale)

    @Override
    public void start(Stage stage) throws IOException {
        this.practitionerService = ServiceLocator.INSTANCE.getCredentialService();
        boolean existingCredential = this.practitionerService.getByLastUpdate() != null;
        if(existingCredential) {
            startWithCredential(stage);
        } else {
            startWithInitialization(stage);
        }
    }

    public void startWithCredential(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RootApplication.class.getResource("/org/example/ostheo_projet/ui/view/connexion.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 600);

        // Init navigation manager
        NavigationManager navigationManager = NavigationManager.getInstance();
        navigationManager.setStage(stage);

        // Init controller with services, if there is no credential entity we let the user to create one
        ConnexionController controller = fxmlLoader.getController();
        controller.initService(ServiceLocator.INSTANCE.getCredentialService(), navigationManager);

        //La scene est le conteneur qui contient tous les éléments graphiques de l'application (boutons, labels, etc.)
        stage.setTitle("Fiche patient");
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/label.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        // Mettre la fenêtre au premier plan et lui donner le focus
        stage.toFront();
        stage.requestFocus();

        Window.addWindow("connexion", stage);

        setupSystemSleepListener();
    }

    public void startWithInitialization(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RootApplication.class.getResource("/org/example/ostheo_projet/ui/view/practitioner-init.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 600);

        // Init navigation manager
        NavigationManager navigationManager = NavigationManager.getInstance();
        navigationManager.setStage(stage);

        // Init controller with services, if there is no credential entity we let the user to create one
        PractitionerInitController controller = fxmlLoader.getController();
        controller.initService(ServiceLocator.INSTANCE.getCredentialService(), navigationManager);

        //La scene est le conteneur qui contient tous les éléments graphiques de l'application (boutons, labels, etc.)
        stage.setTitle("Bienvenue !");
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/label.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        // Mettre la fenêtre au premier plan et lui donner le focus
        stage.toFront();
        stage.requestFocus();

        Window.addWindow("practitioner-init", stage);

        // TODO : l'implémenter ou pas
//        setupSystemSleepListener();
    }

    private void setupSystemSleepListener() {
        if (!Desktop.isDesktopSupported()) {
            System.out.println("Desktop n'est pas supporté sur cette plateforme");
            return;
        } else {
            System.out.println("Desktop est supporté sur cette plateforme");
        }

        Desktop desktop = Desktop.getDesktop();
        SystemSleepListener systemSleepListener = new SystemSleepListener() {
            @Override
            public void systemAboutToSleep(SystemSleepEvent e) {
                System.out.println("Système va se mettre en veille...");
//                javafx.application.Platform.runLater(() -> handleSystemSleep());
            }

            @Override
            public void systemAwoke(SystemSleepEvent e) {
                System.out.println("Système s'est réveillé");
//                javafx.application.Platform.runLater(() -> handleSystemAwoke());
            }
        };

        desktop.addAppEventListener(systemSleepListener);
    }

    private void handleSystemSleep() {
        // Déconnecter l'utilisateur
        System.out.println("Déconnexion automatique - veille détectée");
        applicationContext.setCurrentPractitioner(null);
    }

    private void handleSystemAwoke() {
        // Optionnel : relancer la connexion
        System.out.println("Système réveillé - afficher connexion");


        Window.closeAllWindows();
        Window.createOrShowWindow("connexion", this, "Connexion", "connexion.fxml", false, 600, 500, false);
    }

    private void shutdown() {
        // Cleanup si nécessaire
        System.exit(0);
    }
}