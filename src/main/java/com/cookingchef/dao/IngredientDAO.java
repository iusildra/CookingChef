package com.cookingchef.dao;

import com.cookingchef.model.Ingredient;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IngredientDAO {

    ArrayList<Ingredient> getAllIngredients() throws SQLException;

    Boolean createIngredient(String name, byte[] img, Boolean allergen) throws SQLException;

    void deleteIngredient(int idIngredient) throws SQLException;

    Boolean updateIngredient(int idIngredient, String nameIngredient, byte[] imageIngredient, Boolean allergenIngredient) throws SQLException;

    Ingredient getIngredientById(int idIngredient) throws SQLException;

    Boolean ingredientAlreadyExist(String name) throws SQLException;
}
