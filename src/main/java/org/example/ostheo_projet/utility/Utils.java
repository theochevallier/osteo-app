package org.example.ostheo_projet.utility;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.example.ostheo_projet.enums.DirectoryType;
import org.slf4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Utils {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Utils.class);
    public static final String DEFAULT_APP_DIRECTORY = System.getProperty("user.home") + "/Documents/Ostheo/";
    public static final String DEFAULT_PATIENTS_DIRECTORY = DEFAULT_APP_DIRECTORY + "patients/";
    public static final String DEFAULT_EXPORT_COMPTA_DIRECTORY = DEFAULT_APP_DIRECTORY + "export_compta/";

    public static String getPatientsFilesDirectory() {
        // Create the patients folder if not exists
        File patientsDirectory = new File(DEFAULT_PATIENTS_DIRECTORY);
        if (!patientsDirectory.exists()) {
            if (patientsDirectory.mkdirs()) {
                logger.info("Répertoire patients créé avec succès.");
            } else {
                logger.error("Erreur lors de la création du répertoire patients.");
            }
        }
        return patientsDirectory.getAbsolutePath() + "/";
    }

    /**
     * Format this firstname to have first letter(s) to uppercase
     *
     * @param firstname the string to format
     * @return the formatted string firstname
     */
    public static String formatFirstname(String firstname) {
        if (firstname == null || firstname.isEmpty()) {
            return null;
        }

        StringBuilder formattedFirstname = new StringBuilder();
        String regex = "";
        if (firstname.contains(" ")) {
            regex = " ";
        } else if (firstname.contains("-")) {
            regex = "-";
        } else {
            return firstname.substring(0, 1).toUpperCase() + firstname.substring(1).toLowerCase();
        }

        String[] nameParts = firstname.split(regex);
        for (String part : nameParts) {
            formattedFirstname.append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase()).append(regex);
        }
        formattedFirstname.deleteCharAt(formattedFirstname.length() - 1);
        return formattedFirstname.toString();
    }

    /**
     * Format the string to set the first letter uppercase and other letters not
     *
     * @param string the string to format
     * @return the formatted string
     */
    public static String formatStringFirstLetterUppercase(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static InputStream getIconStream(String extension) {
        String fileName = switch (extension.toLowerCase()) {
            case "pdf" -> "pdf.png";
            case "doc", "docx" -> "word.png";
            case "txt" -> "txt.png";
            default -> "icon-info.png";
        };
        return Utils.class.getResourceAsStream("/data/" + fileName);
    }

    /**
     * Insert an image icon in a button component
     *
     * @param button   the button to add the icon
     * @param iconPath the path to the image icon, ex : "/data/icon.png"
     * @param color    the color to fill the icon
     * @param position the ContentDisplay enum where the icon will be displayed
     */
    public static void insertIconInButton(Button button, String iconPath, Color color, ContentDisplay position) {
        if(iconPath == null || iconPath.isEmpty()){
            button.setGraphic(null);
            return;
        }

        javafx.scene.image.Image iconImage = new Image(Objects.requireNonNull(Utils.class.getResourceAsStream(iconPath)));
        ImageView imageView = new ImageView(iconImage);
        imageView.setFitHeight(14);
        imageView.setFitWidth(14);

        // Change color of icon
        Lighting lighting = new Lighting(new Light.Distant(45, 90, color));
        ColorAdjust bright = new ColorAdjust(0, 1, 1, 1);
        lighting.setContentInput(bright);
        lighting.setSurfaceScale(0.0);
        imageView.setEffect(lighting);

        // Put icon on the position of the button text
        button.setContentDisplay(position);
        button.setGraphic(imageView);
    }

    public static void openFile(File file) {
        try {
            if (!file.exists()) {
                System.out.println("File does not exist.");
                return;
            }
            if (!Desktop.isDesktopSupported()) {
                System.out.println("Open file with desktop error : Desktop is not supported.");
                return;
            }

            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<File> openFileChooser(String title, FileChooser.ExtensionFilter extensionFilter, String initialDirectory, boolean directoryOnly, boolean multipleSelectionAllowed) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        if (extensionFilter == null) {
            System.out.println("Extension filter is null, all files will be shown.");
            extensionFilter = new FileChooser.ExtensionFilter("Tous les fichiers", "*.*");
        }
        fileChooser.getExtensionFilters().addAll(extensionFilter);

        if (directoryOnly) {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("JavaFX Projects");

            File defaultDirectory = new File(initialDirectory);
            chooser.setInitialDirectory(defaultDirectory);

            File selectedDirectory = chooser.showDialog(null);
            if (selectedDirectory != null) {
                return List.of(selectedDirectory);
            } else {
                System.out.println("No directory selected.");
                return null;
            }
        }

        fileChooser.setInitialDirectory(new File(initialDirectory));

        if(multipleSelectionAllowed){
            List<File> files = fileChooser.showOpenMultipleDialog(null);
            if (files != null && !files.isEmpty()) {
                return files;
            } else {
                System.out.println("No file selected.");
                return null;
            }
        }
        List<File> selectedFile = fileChooser.showOpenMultipleDialog(null);
        if (selectedFile != null && !selectedFile.isEmpty()) {
            return List.of(selectedFile.getFirst());
        } else {
            System.out.println("No file selected.");
            return null;
        }
    }

    public static List<File> getFilesOfDirectory(File directory){
        if(directory.exists() && directory.isDirectory()){
            File[] files = directory.listFiles();
            assert files != null;
            return Arrays.asList(files);
        }
        return List.of();
    }

    public static String createPatientConsultDirectoryIfNotExists(int patientId, int consultId) {
        String consultRootPath = getPatientsFilesDirectory() + patientId + "/" + consultId + "/";
        String consultDirPath = consultRootPath + DirectoryType.CONSULTATION.toString().toLowerCase() + "/";
        String comptaDirPath = consultRootPath + DirectoryType.COMPTA.toString().toLowerCase() + "/";
        List<String> directoriesPath = List.of(consultDirPath, comptaDirPath);

        for (String directoryPath : directoriesPath) {
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                try {
                    if (directory.mkdirs()) {
                        System.out.println("Répertoire créé avec succès.");
                    }
                } catch (Exception e) {
                    System.out.println("Error creating directory : " + directoryPath);
                    e.printStackTrace();
                }
            }
        }

        return consultRootPath;
    }

    public static void copyFileToDirectory(String destinationPath, File file) {
        if (file == null) return;

        // copy file to destination directory
        try {
            File destFile = new File(destinationPath + "/" + file.getName());
            java.nio.file.Files.copy(file.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.out.println("Error copying file to patient directory");
            e.printStackTrace();
        }
    }

    public static String normalizeFileName(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFC);
    }

}
