package org.example.ostheo_projet.ui.component;

import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.ostheo_projet.enums.Osteoporose;
import org.example.ostheo_projet.utility.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class OsteoporoseComponent extends HBox {
    @FXML private Button btn1;
    @FXML private Button btn2;
    @FXML private Button btn3;
    @FXML private Button btnCancel;

    private Osteoporose osteoporose;
    private boolean isEditable;
    private static final PseudoClass ACTIVE = PseudoClass.getPseudoClass("active");

    public OsteoporoseComponent(Osteoporose osteoporose, boolean isEditable) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/ostheo_projet/ui/view/osteoporose-component.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
            this.osteoporose = osteoporose;
            this.isEditable = isEditable;
            initComponent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // INIT METHODS

    public void initComponent() {
        // Make the corresponding button active
        // If in show mode (isEditable = false), disable the buttons and hide the cancel button (arrange style border radius)
        btn1.setDisable(!isEditable);
        btn2.setDisable(!isEditable);
        btn3.setDisable(!isEditable);
        btnCancel.setVisible(isEditable);
        btnCancel.setManaged(isEditable);

        if(isEditable){
            btnCancel.setStyle("-fx-border-radius: 8 0 0 8; -fx-background-radius: 8 0 0 8;");
        } else {
            btn1.setStyle("-fx-border-radius: 8 0 0 8; -fx-background-radius: 8 0 0 8;");
        }

        switch (this.osteoporose) {
            case Osteoporose.VERT:
                setActive(btn1);
                btn1.setDisable(false);
                setInactive(btn2);
                setInactive(btn3);
                setInactive(btnCancel);
                break;
            case Osteoporose.ORANGE:
                setInactive(btn1);
                setActive(btn2);
                btn2.setDisable(false);
                setInactive(btn3);
                setInactive(btnCancel);
                break;
            case Osteoporose.ROUGE:
                setInactive(btn1);
                setInactive(btn2);
                setActive(btn3);
                btn3.setDisable(false);
                setInactive(btnCancel);
                break;
            case Osteoporose.AUCUN:
                setInactive(btn1);
                setInactive(btn2);
                setInactive(btn3);
                setActive(btnCancel);
                btnCancel.setDisable(false);
                break;
            default:
                setNeutral(btn1);
                setNeutral(btn2);
                setNeutral(btn3);
                setNeutral(btnCancel);
                break;
        }

        if(!isEditable) {
            btn1.setFocusTraversable(false);
            btn2.setFocusTraversable(false);
            btn3.setFocusTraversable(false);
            btn1.setMouseTransparent(true);
            btn2.setMouseTransparent(true);
            btn3.setMouseTransparent(true);
        }
    }

    // OTHER METHODS

    public void setActive(Button button){
        if(button.getId().equals("btnCancel")){
            Utils.insertIconInButton(button, "/data/delete.png", Color.BLACK, ContentDisplay.CENTER);
        } else {
            Utils.insertIconInButton(button, "/data/check.png", Color.WHITE, ContentDisplay.CENTER);
        }
        button.pseudoClassStateChanged(ACTIVE, true);
    }

    public void setInactive(Button button){
        button.setGraphic(null);
        button.pseudoClassStateChanged(ACTIVE, false);
    }

    public void setNeutral(Button button){
        button.pseudoClassStateChanged(ACTIVE, false);
    }

    public void setOsteoporose(Osteoporose osteoporose) {
        this.osteoporose = osteoporose;
        initComponent();
    }

    public Osteoporose getOsteoporose() {
        return this.osteoporose;
    }

    public boolean isEditable() {
        return this.isEditable;
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
        initComponent();
    }

    // HANDLE METHODS

    public void handleClicCancel(){
        this.osteoporose = Osteoporose.AUCUN;
        initComponent();
    }

    public void handleClicBtn1() {
        this.osteoporose = Osteoporose.VERT;
        initComponent();
    }

    public void handleClicBtn2() {
        this.osteoporose = Osteoporose.ORANGE;
        initComponent();
    }

    public void handleClicBtn3(){
        this.osteoporose = Osteoporose.ROUGE;
        initComponent();
    }

}
