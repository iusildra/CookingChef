package com.cookingchef.controller;


import com.cookingchef.facade.AdFacade;
import com.cookingchef.facade.IngredientFacade;
import com.cookingchef.model.Ad;
import com.cookingchef.model.Ingredient;
import com.cookingchef.model.Partner;
import com.cookingchef.view.Main;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdController implements Initializable {

    @FXML
    private ListView<Ad> adListView = new ListView<Ad>();

    @FXML
    private Button cancelButton;

    @FXML
    private Button validateButton;

    @FXML
    private TextField formDescription;

    @FXML
    private TextField formPrice;

    @FXML
    private ComboBox<Ingredient> ingredientComboBox = new ComboBox<Ingredient>();

    @FXML
    private ComboBox<Partner> partnerComboBox = new ComboBox<Partner>();

    @FXML
    private Stage secondaryStage;


    private IngredientFacade ingredientFacade;
    private AdFacade adFacade;
    private PartnerFacade partnerFacade;


    public AdController() {
        this.adFacade = AdFacade.getAdFacade();
        this.ingredientFacade = IngredientFacade.getIngredientFacade();
        this.partnerFacade = PartnerFacade.getPartnerFacade();
    }

    public boolean createAd(String description, int price, int partnerId, int ingredientId) {
        try {
            Ad ad = new Ad(description, price, partnerId, ingredientId);
            if (this.adFacade.addAd(ad).isPresent()) {
                return true;
            }else{
                return false;
            }
        } catch (SQLException e) {
            Notifications.create()
                    .title("Erreur")
                    .text("Erreur lors de la création")
                    .showError();
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteAd(Ad ad) {
        try {
            this.adFacade.deleteAd(ad);

            this.adListView.getItems().remove(ad);
            Notifications.create()
                    .title("Succès")
                    .text("La publicité a été supprimée avec succès")
                    .showConfirm();
        } catch (SQLException e) {
            Notifications.create()
                    .title("Erreur")
                    .text("Erreur lors de la suppression")
                    .showError();
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean updateAd(int adId, String description, int price, int partnerId, int ingredientId) {
        try {
            Ad ad = new Ad(adId,description, price, partnerId, ingredientId);
            this.adFacade.updateAd(ad);
            return true;
        } catch (SQLException e) {
            Notifications.create()
                    .title("Erreur")
                    .text("Erreur lors de la modification")
                    .showError();
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void showForm() {
        // Création de la seconde fenêtre
        this.secondaryStage = new Stage();
        secondaryStage.initModality(Modality.WINDOW_MODAL);
        secondaryStage.initOwner(Main.stage);

        Label labelDescription = new Label("Entrer la description de votre pub :");
        this.formDescription = new TextField();

        Label labelPrice = new Label("Entrer le prix de votre pub :");
        this.formPrice = new TextField();

        Label labelPartner = new Label("Choisir le partenaire :");
        this.partnerComboBox = new ComboBox<Partner>();
        this.partnerComboBox.setItems(FXCollections.observableArrayList(this.partnerFacade.getAllPartners()));

        Label labelIngredient = new Label("Choisir l'ingrédient de votre pub :");
        this.ingredientComboBox = new ComboBox<Ingredient>();
        try {
            this.ingredientComboBox.setItems(FXCollections.observableArrayList(this.ingredientFacade.getAllIngredients()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.cancelButton = new Button("Annuler");
        this.cancelButton.setOnAction(actionEvent -> secondaryStage.close());

        this.validateButton = new Button("Valider");

        HBox first = new HBox(labelDescription, this.formDescription);
        first.setSpacing(10);
        first.setPadding(new Insets(10));
        first.setAlignment(Pos.CENTER);

        HBox second = new HBox(labelPrice, this.formPrice);
        second.setSpacing(10);
        second.setPadding(new Insets(10));
        second.setAlignment(Pos.CENTER);

        HBox third = new HBox(labelIngredient, this.ingredientComboBox);
        third.setSpacing(10);
        third.setPadding(new Insets(10));
        third.setAlignment(Pos.CENTER);

        HBox fourth = new HBox(labelPartner, this.partnerComboBox);
        fourth.setSpacing(10);
        fourth.setPadding(new Insets(10));
        fourth.setAlignment(Pos.CENTER);

        HBox five = new HBox(this.cancelButton, this.validateButton);
        fourth.setSpacing(10);
        fourth.setPadding(new Insets(10));
        fourth.setAlignment(Pos.CENTER);

        VBox root = new VBox(first, second, third, fourth, five);
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 600, 400);
        this.secondaryStage.setScene(scene);
        this.secondaryStage.show();
    }

    public void showFormCreate() {
        // Afficher le formulaire général de create Ingredient
        this.showForm();

        // Définir le bouton valider pour le create form
        this.validateButton.setOnAction(actionEvent -> {

            //Si champ description est vide
            if (this.formDescription.getText().isEmpty()) {
                Notifications.create()
                        .title("Information")
                        .text("Veuillez donner une description à votre publicité")
                        .showWarning();
                return;
            }

            //Si champ prix est vide
            if (this.formPrice.getText().isEmpty()) {
                Notifications.create()
                        .title("Information")
                        .text("Veuillez donner un prix à votre publicité")
                        .showWarning();
                return;
            }

            //Si le champ prix n'est pas un nombre
            if (!this.formPrice.getText().matches("[0-9]+")) {
                Notifications.create()
                        .title("Information")
                        .text("Veuillez donner un prix valide")
                        .showWarning();
                return;
            }

            //Si le champ partner n'est pas selectionné
            if (this.partnerComboBox.getValue().equals(null)) {
                Notifications.create()
                        .title("Information")
                        .text("Veuillez selectionner un partenaire")
                        .showWarning();
                return;
            }

            //Si le champ ingredient n'est pas selectionné
            if (this.ingredientComboBox.getValue().equals(null)) {
                Notifications.create()
                        .title("Information")
                        .text("Veuillez selectionner un ingredient")
                        .showWarning();
                return;
            }

            if (this.createAd(this.formDescription.getText(), Integer.parseInt(this.formPrice.getText()), this.partnerComboBox.getValue().getId().get(), this.ingredientComboBox.getValue().getId())) {
                this.secondaryStage.close();
                Notifications.create()
                        .title("Succès")
                        .text("La publicité a été créée avec succès")
                        .showConfirm();
                this.showList();
            } else {
                Notifications.create()
                        .title("Erreur")
                        .text("Erreur lors de la création")
                        .showWarning();
            }
        });
    }

    public void showFormUpdate(Ad ad){
        // Afficher le formulaire général du update form
        this.showForm();

        // Remplir avec les informations existantes
        this.formDescription.setText(ad.getDescriptionPromotion());
        this.formPrice.setText(String.valueOf(ad.getPrice()));
        this.partnerComboBox.setValue(this.partnerFacade.getPartnerById(ad.getPartnerId()));
        try {
            this.ingredientComboBox.setValue(this.ingredientFacade.getIngredientById(ad.getIngredientId()));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Définir le bouton valider pour le update form
        this.validateButton.setOnAction(actionEvent -> {

            //Si champ description est vide
            if (this.formDescription.getText().isEmpty()) {
                Notifications.create()
                        .title("Information")
                        .text("Veuillez donner une description à votre publicité")
                        .showWarning();
                return;
            }

            //Si champ prix est vide
            if (this.formPrice.getText().isEmpty()) {
                Notifications.create()
                        .title("Information")
                        .text("Veuillez donner un prix à votre publicité")
                        .showWarning();
                return;
            }

            //Si le champ prix n'est pas un nombre
            if (!this.formPrice.getText().matches("[0-9]+")) {
                Notifications.create()
                        .title("Information")
                        .text("Veuillez donner un prix valide")
                        .showWarning();
                return;
            }

            //Si le champ partner n'est pas selectionné
            if (this.partnerComboBox.getValue().equals(null)) {
                Notifications.create()
                        .title("Information")
                        .text("Veuillez selectionner un partenaire")
                        .showWarning();
                return;
            }

            //Si le champ ingredient n'est pas selectionné
            if (this.ingredientComboBox.getValue().equals(null)) {
                Notifications.create()
                        .title("Information")
                        .text("Veuillez selectionner un ingredient")
                        .showWarning();
                return;
            }

            if (this.updateAd(ad.getId().get(), this.formDescription.getText(), Integer.parseInt(this.formPrice.getText()), this.partnerComboBox.getValue().getId().get(), this.ingredientComboBox.getValue().getId())) {
                this.secondaryStage.close();
                Notifications.create()
                        .title("Sucess")
                        .text("La publicité a été mise à jour avec succès")
                        .showConfirm();
                this.showList();
            } else {
                Notifications.create()
                        .title("Erreur")
                        .text("Erreur lors de la mise à jour")
                        .showWarning();
            }

        });
    }

    public ArrayList<Ad> showList() {

        ArrayList<Ad> ads;

        try {
            ads = this.adFacade.getAllAds();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        if (ads == null) {
            Notifications.create()
                    .title("Erreur")
                    .text("Erreur avec la base de donnée\nRelancer l'application")
                    .showError();
        }
        return ads;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Ad> ads = FXCollections.observableArrayList(this.showList());

        IngredientFacade ingredientFacade1 = IngredientFacade.getIngredientFacade();
        PartnerFacade partnerFacade1 = PartnerFacade.getPartnerFacade();

        adListView.setItems(ads);

        adListView.setCellFactory(param -> new ListCell<>() {

            @Override
            protected void updateItem(Ad ad, boolean empty) {
                super.updateItem(ad, empty);

                if (empty || ad == null || ad.getDescriptionPromotion() == null) {
                    setText(null);
                } else {
                    HBox hBox = new HBox(5);
                    Label descriptionLabel = new Label(ad.getDescriptionPromotion());
                    Label priceLabel = new Label(String.valueOf(ad.getPrice()));
                    Label partnerLabel = new Label(partnerFacade1.getPartnerById(ad.getPartnerId()).getName());
                    Label ingredientLabel;
                    try {
                        ingredientLabel = new Label(ingredientFacade1.getIngredientById(ad.getIngredientId()).getName());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }

                    Button deleteButton = new Button("Supprimer");
                    deleteButton.setOnAction(actionEvent -> {
                        Ad adToDelete = getItem();
                        // code de suppression de l'ingrédient de la ListView
                        deleteAd(adToDelete);
                    });

                    Button updateButton = new Button("Modifer");
                    updateButton.setOnAction(actionEvent -> {
                        Ad adToUpdate = getItem();
                        // code de modification de l'ingrédient de la ListView
                        showFormUpdate(adToUpdate);
                    });

                    hBox.getChildren().addAll(descriptionLabel, priceLabel, partnerLabel, ingredientLabel, deleteButton, updateButton);
                    setGraphic(hBox);
                }
            }
        });
    }
}
