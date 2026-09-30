package org.example.ostheo_projet.ui.component;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class is a TextField which implements an "autocomplete" functionality, based on a supplied list of entries.
 * @author Caleb Brinkman
 */
public class AutoCompleteTextField extends TextField
{
    /** The existing autocomplete entries. */
    private final SortedSet<String> entries;
    /** The popup used to select an entry. */
    private ContextMenu entriesPopup;

    /** Construct a new AutoCompleteTextField. */
    public AutoCompleteTextField() {
        super();
        entries = new TreeSet<>();
        entriesPopup = new ContextMenu();

        // Pouvoir naviguer avec la touche bas pour sélectionner la ville
        setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("DOWN")) {
                if (entriesPopup.isShowing() && !entriesPopup.getItems().isEmpty()) {
                    CustomMenuItem firstItem = (CustomMenuItem) entriesPopup.getItems().get(0);
                    ((Label) firstItem.getContent()).requestFocus();
                    event.consume();
                }
            }
        });

        // Input the text when user clicks on a popup entry
        entriesPopup.setOnShowing(event -> {
            if (!entriesPopup.getItems().isEmpty()) {
                CustomMenuItem firstItem = (CustomMenuItem) entriesPopup.getItems().get(0);
                ((Label) firstItem.getContent()).requestFocus();
                event.consume();
            }
        });

        textProperty().addListener((observableValue, s, s2) -> {
            if (getText().isEmpty())
            {
                entriesPopup.hide();
            } else
            {
                LinkedList<String> searchResult = new LinkedList<>();
//                    searchResult.addAll(entries.subSet(getText(), getText() + Character.MAX_VALUE));
                String lowerInput = getText().toLowerCase();
                for (String entry : entries) {
                    if (entry.toLowerCase().startsWith(lowerInput) || entry.toLowerCase().contains(lowerInput)) {
                        searchResult.add(entry);
                    }
                }
                if (!entries.isEmpty())
                {
                    populatePopup(searchResult);
                    if (!entriesPopup.isShowing())
                    {
                        entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
                    }
                } else
                {
                    entriesPopup.hide();
                }
            }
        });

        focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                entriesPopup.hide();
            }
        });

    }

    /**
     * Get the existing set of autocomplete entries.
     * @return The existing autocomplete entries.
     */
    public SortedSet<String> getEntries() { return entries; }

    /**
     * Populate the entry set with the given search results.  Display is limited to 10 entries, for performance.
     * @param searchResult The set of matching strings.
     */
    private void populatePopup(List<String> searchResult) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        // If you'd like more entries, modify this line.
        int maxEntries = 10;
        int count = Math.min(searchResult.size(), maxEntries);
        for (int i = 0; i < count; i++)
        {
            final String result = searchResult.get(i);
            Label entryLabel = new Label(result);
            // Bind width of labels to autocomplete width
            entryLabel.prefWidthProperty().bind(this.widthProperty());
            entryLabel.setWrapText(true);
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            item.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent actionEvent) {
                    setText(result);
                    entriesPopup.hide();
                }
            });
            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);

    }

    public void hidePopup() {
        entriesPopup.hide();
    }
}