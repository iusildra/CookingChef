package com.cookingchef.controller;

import com.cookingchef.facade.IngredientFacade;
import com.cookingchef.model.Ingredient;
import com.cookingchef.view.Main;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * The type Ingredient controller.
 */
public class IngredientController implements Initializable {
    @FXML
    private TextField nameIngredient;

   @FXML
   private ImageView imageView;

   @FXML
   private Button addImage;

   @FXML
   private CheckBox checkBox;

   @FXML
   private Button cancelButton;

   @FXML
   private Button validateButton;

    @FXML
    private ListView<Ingredient> ingredientList = new ListView<>();

    @FXML
    private Stage secondaryStage;

    private final IngredientFacade ingredientFacade;

    /**
     * Instantiates a new Ingredient controller.
     */
    public IngredientController() {
        this.ingredientFacade = IngredientFacade.getIngredientFacade();
    }

    /**
     * Fetch list.
     */
    public void fetchList() {
        try {
            this.ingredientList.setItems(FXCollections.observableArrayList(this.ingredientFacade.getAllIngredients()));
            this.ingredientList.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
            Notifications.create()
                    .title("Erreur")
                    .text("Erreur avec la base de donnée\nRelancer l'application")
                    .showError();
        }
    }

    private byte[] addImageIngredient(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                return Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                // TODO : gérer exception
                e.printStackTrace();
            }
        }
        return null;
    }

    private byte[] recupImage() {
        // Récupérer l'image séléctionnée
        PixelReader pixelReader = imageView.getImage().getPixelReader();
        int width = (int) imageView.getImage().getWidth();
        int height = (int) imageView.getImage().getHeight();

        BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bImage.setRGB(x, y, pixelReader.getArgb(x, y));
            }
        }

        byte[] imageData = new byte[0];
        try {
            File tempFile = File.createTempFile("image", ".tmp");
            ImageIO.write(bImage, "png", tempFile);

            imageData = Files.readAllBytes(tempFile.toPath());
            tempFile.delete();
        } catch (IOException e) {
            // TODO : gérer l'exception
            e.printStackTrace();
        }

        return imageData;
    }

    /**
     * Create ingredient.
     *
     * @param nameIngredient  the name ingredient
     * @param imageIngredient the image ingredient
     * @param isAllergen      the is allergen
     */
    public void createIngredient(String nameIngredient, byte[] imageIngredient, boolean isAllergen) {
        try {
            if (this.ingredientFacade.createIngredient(nameIngredient, imageIngredient, isAllergen)) {
                this.secondaryStage.close();
                Notifications.create()
                        .title("Succès")
                        .text("L'ingrédient a été créé avec succès")
                        .showConfirm();
                this.fetchList();
            } else {
                Notifications.create()
                        .title("Erreur")
                        .text("Nom déjà utilisé")
                        .showWarning();
            }
        } catch (SQLException e) {
            Notifications.create()
                    .title("Erreur")
                    .text("Erreur lors de la création")
                    .showError();
            e.printStackTrace();
        }
    }

    /**
     * Delete ingredient.
     *
     * @param idIngredient the id ingredient
     */
    public void deleteIngredient(int idIngredient) {
        try {
            this.ingredientFacade.deleteIngredient(idIngredient);

            int i = 0;
            while (this.ingredientList.getItems().get(i).getId() != idIngredient) {
                i++;
            }
            this.ingredientList.getItems().remove(i);
            Notifications.create()
                    .title("Succès")
                    .text("L'ingrédient a été supprimé avec succès")
                    .showConfirm();
        } catch (SQLException e) {
            Notifications.create()
                    .title("Erreur")
                    .text("Erreur lors de la suppression")
                    .showError();
            e.printStackTrace();
        }
    }

    /**
     * Update ingredient.
     *
     * @param idIngredient    the id ingredient
     * @param nameIngredient  the name ingredient
     * @param imageIngredient the image ingredient
     * @param isAllergen      the is allergen
     */
    public void updateIngredient(int idIngredient, String nameIngredient, byte[] imageIngredient, boolean isAllergen) {
        try {
            if (this.ingredientFacade.updateIngredient(idIngredient, nameIngredient, imageIngredient, isAllergen)) {
                this.secondaryStage.close();
                Notifications.create()
                        .title("Sucess")
                        .text("L'ingrédient a été mis à jour avec succès")
                        .showConfirm();
                this.fetchList();
            } else {
                Notifications.create()
                        .title("Erreur")
                        .text("Nom déjà utilisé")
                        .showWarning();
            }
        } catch (SQLException e) {
            Notifications.create()
                    .title("Erreur")
                    .text("Erreur lors de la modification")
                    .showError();
            e.printStackTrace();
        }
    }

    /**
     * Show form.
     */
    public void showForm() {
        // Création de la seconde fenêtre
        this.secondaryStage = new Stage();
        secondaryStage.initModality(Modality.WINDOW_MODAL);
        secondaryStage.initOwner(Main.getStage());

        Label labelName = new Label("Entrer le nom de l'ingrédient :");
        this.nameIngredient = new TextField();

        this.imageView = new ImageView();
        this.imageView.setFitHeight(150);
        this.imageView.setFitWidth(188.0);
        this.imageView.setPickOnBounds(true);
        this.imageView.setPreserveRatio(true);
        this.addImage = new Button("Importer image");
        addImage.setOnAction(actionEvent -> {
            byte[] bufferImage = this.addImageIngredient(this.secondaryStage);
            if (bufferImage != null) {
                this.imageView.setImage(new Image(new ByteArrayInputStream(bufferImage)));
            } else {
                Notifications.create()
                        .title("Information")
                        .text("Image inchangée")
                        .showInformation();
            }
        });

        Label labelCheckBox = new Label("Allergène");
        this.checkBox = new CheckBox();

        this.cancelButton = new Button("Annuler");
        this.cancelButton.setOnAction(actionEvent -> secondaryStage.close());

        this.validateButton = new Button("Valider");

        HBox first = new HBox(labelName, this.nameIngredient);
        first.setSpacing(10);
        first.setPadding(new Insets(10));
        first.setAlignment(Pos.CENTER);

        HBox second = new HBox(this.imageView, this.addImage);
        second.setSpacing(10);
        second.setPadding(new Insets(10));
        second.setAlignment(Pos.CENTER);

        HBox third = new HBox(labelCheckBox, this.checkBox);
        third.setSpacing(10);
        third.setPadding(new Insets(10));
        third.setAlignment(Pos.CENTER);

        HBox fourth = new HBox(this.cancelButton, this.validateButton);
        fourth.setSpacing(10);
        fourth.setPadding(new Insets(10));
        fourth.setAlignment(Pos.CENTER);

        VBox root = new VBox(first, second, third, fourth);
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 600, 400);
        this.secondaryStage.setScene(scene);
        this.secondaryStage.show();
    }

    /**
     * Show form create.
     */
    public void showFormCreate() {
        // Afficher le formulaire général de create Ingredient
        this.showForm();

        // Définir le bouton valider pour le create form
        this.validateButton.setOnAction(actionEvent -> {

            if (this.nameIngredient.getText().isEmpty()) {
                Notifications.create()
                        .title("Information")
                        .text("Veuillez remplir le nom de l'ingrédient")
                        .showWarning();
                return;
            }

            byte[] imageData = new byte[0];
            if (this.imageView.getImage() == null) {
                Notifications.create()
                        .title("Information")
                        .text("Il est recommandé de mettre une image pour l'ingrédient")
                        .showInformation();
            } else {
                imageData = this.recupImage();
            }

            this.createIngredient(this.nameIngredient.getText(), imageData, this.checkBox.isSelected());
        });
    }

    /**
     * Show form update.
     *
     * @param ingredientToUpdate the ingredient to update
     */
    public void showFormUpdate(Ingredient ingredientToUpdate) {
        // Afficher le formulaire général du update form
        this.showForm();

        // Remplir avec les informations existantes
        this.nameIngredient.setText(ingredientToUpdate.getName());
        if (ingredientToUpdate.getImage() != null) {
            this.imageView.setImage(new Image(new ByteArrayInputStream(ingredientToUpdate.getImage())));
        }
        this.checkBox.setSelected(ingredientToUpdate.getAllergen());

        // Définir le bouton valider pour le update form
        this.validateButton.setOnAction(actionEvent -> {

            if (this.nameIngredient.getText().isEmpty()) {
                Notifications.create()
                        .title("Information")
                        .text("Veuillez remplir le nom de l'ingrédient")
                        .showWarning();
                return;
            }
            byte[] imageData = new byte[0];
            if (this.imageView.getImage() == null) {
                Notifications.create()
                        .title("Information")
                        .text("Il est recommandé de choisir une image pour l'ingrédient")
                        .showInformation();
            } else {
                imageData = this.recupImage();
            }

            this.updateIngredient(ingredientToUpdate.getId(), this.nameIngredient.getText(), imageData, this.checkBox.isSelected());
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.ingredientList.setCellFactory(param -> cellFactory());
        fetchList();
    }

    private ListCell<Ingredient> cellFactory() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Ingredient ingredient, boolean empty) {
                super.updateItem(ingredient, empty);

                if (empty || ingredient == null || ingredient.getName() == null) {
                    setText(null);
                } else {
                    VBox vBox = new VBox();
                    Label label = new Label(ingredient.getName());
                    ImageView imageView = new ImageView();
                    if (ingredient.getImage() != null) {
                        imageView = new ImageView(new Image(new ByteArrayInputStream(ingredient.getImage())));
                        imageView.setFitHeight(50);
                        imageView.setFitWidth(50);
                    }

                    Button deleteButton = new Button("Supprimer");
                    deleteButton.setOnAction(actionEvent -> {
                        Ingredient ingredientToDelete = getItem();
                        // code de suppression de l'ingrédient de la ListView
                        deleteIngredient(ingredientToDelete.getId());
                    });

                    Button updateButton = new Button("Modifer");
                    updateButton.setOnAction(actionEvent -> {
                        Ingredient ingredientToUpdate = getItem();
                        // code de modification de l'ingrédient de la ListView
                        showFormUpdate(ingredientToUpdate);
                    });

                    if (ingredient.getAllergen()) {
                        label.setStyle("-fx-text-fill: red");
                    }

                    HBox hBox = new HBox(deleteButton, updateButton);
                    hBox.setSpacing(10);
                    hBox.setPadding(new Insets(10));
                    hBox.setAlignment(Pos.CENTER);

                    vBox.getChildren().addAll(imageView, label, hBox);
                    setGraphic(vBox);
                }
            }
        };
    }
}
