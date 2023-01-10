package com.cookingchef.controller;

import com.cookingchef.dao.Postgres.PostgresCategoryDAO;
import com.cookingchef.facade.AdminSuggestionFacade;
import com.cookingchef.model.Suggestion;
import com.cookingchef.model.Category;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SuggestionFormController implements Initializable {
  @FXML
  private Label welcomeText;

  @FXML
  private TextField formTitle;

  @FXML
  private TextField formDescription;

  @FXML
  private ComboBox<Category> formCategory;

  @FXML
  private Button closeButton;

  private Optional<Integer> userId = Optional.of(0);// TODO: patch to use logged user
  private Optional<Integer> suggestionId = Optional.empty();
  private ObservableList<Category> categories;

  private Optional<Runnable> callback = Optional.empty();

  @FXML
  protected void onClickValidateButton() {
    if (!Middlewares.mustBeLoggedIn(userId)) {
      Popups.errorPopup("You must be logged in to create a suggestion");
      return;
    }

    if (!checkInputs()) {
      Popups.errorPopup("Please fill all fields");
      return;
    }

    var value = this.formCategory.getValue();
    AdminSuggestionFacade suggestionFacade = AdminSuggestionFacade.getAdminSuggestionFacade();
    var suggestion = new Suggestion(this.suggestionId, this.formTitle.getText(), this.formDescription.getText(),
        value.getIdCategory(),
        value.getNameCategory(), this.userId.get());

    if (suggestion.getId().isEmpty()) {
      try {
        suggestionFacade.addSuggestion(suggestion);
      } catch (SQLException e) {
        Popups.errorPopup("Creation failed");
        return;
      }
    } else {
      try {
        suggestionFacade.updateSuggestion(suggestion);
      } catch (SQLException e) {
        Popups.errorPopup("Update failed");
        return;
      }
    }

    callback.ifPresent(Runnable::run);
    this.close();
  }

  public void reset() {
    this.userId = Optional.empty();
    this.suggestionId = Optional.empty();
  }

  public void fillInputs(Suggestion suggestion) {
    this.suggestionId = suggestion.getId();
    this.formTitle.setText(suggestion.getTitle());
    this.formDescription.setText(suggestion.getDescription());
    this.formCategory.setValue(this.categories.filtered(x -> x.getIdCategory() == suggestion.getCategoryId()).get(0));
  }

  public boolean checkInputs() {
    return this.formTitle.getText().length() > 0 && this.formDescription.getText().length() > 0
        && this.formCategory.getValue() != null;
  }

  public void setUserId(Optional<Integer> userId) {
    this.userId = userId;
  }

  public void setCallback(Runnable callback) {
    this.callback = Optional.of(callback);
  }

  @FXML
  protected void close() {
    var stage = (Stage) closeButton.getScene().getWindow();
    stage.close();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      this.categories = FXCollections
          .observableArrayList(PostgresCategoryDAO.getPostgresCategoryDAO().getAllCategories().stream()
              .filter(x -> x.getKey().equals("Suggestion")).map(x -> x.getValue()).collect(Collectors.toList()));
      this.formCategory.setItems(this.categories);
    } catch (SQLException e) {
      Popups.errorPopup("Failed to load categories");
    }
  }
}