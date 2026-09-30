package org.example.ostheo_projet.utility;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.example.ostheo_projet.model.City;
import org.example.ostheo_projet.service.CityService;
import org.example.ostheo_projet.ui.component.AutoCompleteTextField;
import org.w3c.dom.Text;

import java.util.List;
import java.util.function.Consumer;

public class Form {

    public enum PerformAction {
        CLEAR,
        DISABLE,
        ENABLE,
        HIDE,
        SHOW
    }

    /**
     * Utility method that add listeners to textfields to format texts : put lastname to uppercase, check case for firstname
     * @param inputLastname the inputLastname element
     * @param inputFirstname the inputFirstname element
     */
    public static void initInputFormatters(TextField inputLastname, TextField inputFirstname) {
        // Put lastname of user in input to upper case automatically
        inputLastname.textProperty().addListener((ov, oldValue, newValue) -> {
            inputLastname.setText(newValue.toUpperCase());
        });

        // format firstname of user in input on focus event changed
        inputFirstname.focusedProperty().addListener((ov, oldValue, newValue) -> {
            inputFirstname.setText(Utils.formatFirstname(inputFirstname.getText()));
        });
    }


    /**
     * Fill the autocompletecity element with data, and put the postal code of the city selected by user
     * @param autoCompleteCity the autocomplete text field
     * @param inputPostalCode the postal code input
     * @param cities the list of cities from the database
     * @param cityService the city service
     */
    public static void initAutoCompleteCity(AutoCompleteTextField autoCompleteCity, TextField inputPostalCode, List<City> cities, CityService cityService){
        // Remplir la liste des villes pour l'autocomplete et event listener pour ajouter le code postal automatiquement
        // Construct a list of string with the cities names and postal codes
        List<String> citiesNames = cities.stream().map(city -> city.getName() + ", " + city.getPostalCode()).toList();
        autoCompleteCity.getEntries().addAll(citiesNames);

        autoCompleteCity.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.contains(",")) return;

            String cityName = newValue.split(",")[0];
            String postalCode = newValue.split(", ")[1];
            City city = cityService.getByNamePostalCode(cityName, postalCode);
            if(city != null){
                inputPostalCode.setText(city.getPostalCode());
                autoCompleteCity.setText(city.getName());
            } else {
                inputPostalCode.clear();
            }
        });
    }

    public static TextField checkTextFieldsAreFilled(TextField... textFields){
        for(TextField textField : textFields){
            if(textField.getText() == null || textField.getText().isEmpty()){
                return textField;
            }
        }
        return null;
    }

    /**
     * Change the scroll speed of a scrollpane, with a multiplier.
     * @param scrollPane the scrollpane to change the speed of
     * @param scrollSpeed the multiplier to change the speed of the scrollpane
     */
    public static void changeScrollSpeed(ScrollPane scrollPane, double scrollSpeed){
        // Augmenter la vitesse de scroll vertical
        scrollPane.getContent().setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            double vvalue = scrollPane.getVvalue();

            // Augmenter le multiplicateur pour plus de vitesse (par défaut ~0.05)
            scrollPane.setVvalue(vvalue - (deltaY / 1000) * scrollSpeed);
        });
    }

    /**
     * Check if the email is valid, with a regex.
     * @param email the email to check
     * @return true if the email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    public static <T extends Node> void forEachNodeOfType(
            Node node,
            Class<T> targetType,
            Consumer<T> action) {

        if (targetType.isInstance(node)) {
            action.accept(targetType.cast(node));
        }

        if (node instanceof Parent parent) {
            parent.getChildrenUnmodifiable().forEach(child -> forEachNodeOfType(child, targetType, action));
        }
    }

    public static void enableTabNextNode(TextArea textArea, Node nextNode) {
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.TAB && !e.isShiftDown()) {
                e.consume();
                nextNode.requestFocus();
            }
        });
    }

    public static void addListenerDateFormater(TextField inputDate, TextField inputTime){
        // Automatically format date to jj/mm/aaaa
        inputDate.textProperty().addListener((ov, oldValue, newValue) -> {
            if(newValue.length() > oldValue.length() && (newValue.length() == 2 || newValue.length() == 5)){
                inputDate.setText(newValue + "/");
            }
        });

        // only allow numbers in input date
        inputDate.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,2}(/\\d{0,2}(/\\d{0,4})?)?") || newText.isEmpty()) {
                return change;
            }
            return null;
        }));

        if(inputTime != null){
            // For time textfield, only allow two numbers : two numbers for time field
            inputTime.textProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue.length() > oldValue.length() && newValue.length() == 2){
                    inputTime.setText(newValue + ":");
                }
            });
            inputTime.setTextFormatter(new TextFormatter<>(change -> {
                String newText = change.getControlNewText();
                if (newText.matches("\\d{0,2}(:\\d{0,2})?") || newText.isEmpty()) {
                    return change;
                }
                return null;
            }));
        }
    }

    public static void addListenerNumbersFormater(TextField input){
        input.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("^[0-9]+$") || newText.isEmpty()) {
                return change;
            }
            return null;
        }));
    }
}
