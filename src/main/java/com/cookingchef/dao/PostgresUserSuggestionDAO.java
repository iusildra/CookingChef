package com.cookingchef.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.cookingchef.dbutils.ConnectionManager;
import com.cookingchef.model.Suggestion;
import com.cookingchef.model.SuggestionDbFields;

public class PostgresUserSuggestionDAO implements UserSuggestionDAO {

  private static AtomicReference<PostgresUserSuggestionDAO> instance = new AtomicReference<>();

  protected PostgresUserSuggestionDAO() {
  }

  public static PostgresUserSuggestionDAO getPostgresUserSuggestionDAO() {
    instance.compareAndSet(null, new PostgresUserSuggestionDAO());
    return instance.get();
  }

  @Override
  public Optional<Integer> createSuggestionInDb(Suggestion suggestion) throws SQLException {
    var query = "INSERT INTO suggestion (title, description, category, user_id) VALUES (?, ?, ?, ?) RETURNING id";
    var conn = ConnectionManager.getConnection();

    try (var stmt = conn.prepareStatement(query)) {

      stmt.setString(1, suggestion.getTitle());
      stmt.setString(2, suggestion.getDescription());
      stmt.setInt(3, suggestion.getCategoryId());
      stmt.setInt(4, suggestion.getAuthorId());
      var rs = stmt.executeQuery();

      if (rs.next()) {
        var newId = rs.getInt(SuggestionDbFields.ID.value);
        suggestion.setId(newId);
        return Optional.of(rs.getInt(SuggestionDbFields.ID.value));
      }
    }

    return Optional.empty();
  }

  @Override
  public Optional<Suggestion> getSuggestionById(int id) throws SQLException {
    var query = "SELECT * FROM suggestion WHERE id = ?";
    var conn = ConnectionManager.getConnection();

    try (var stmt = conn.prepareStatement(query)) {
      stmt.setInt(1, id);

      var rs = stmt.executeQuery();

      if (rs.next()) {
        return Optional.of(new Suggestion(
            Optional.of(rs.getInt(SuggestionDbFields.ID.value)),
            rs.getString(SuggestionDbFields.TITLE.value),
            rs.getString(SuggestionDbFields.DESCRIPTION.value),
            rs.getInt(SuggestionDbFields.CATEGORY_ID.value),
            rs.getInt(SuggestionDbFields.AUTHOR_ID.value)));
      }
    }
    return Optional.empty();
  }

  @Override
  public List<Suggestion> getSuggestionsByTitle(String title) throws SQLException {
    var query = "SELECT * FROM suggestion WHERE title LIKE ?";
    var conn = ConnectionManager.getConnection();

    try (var stmt = conn.prepareStatement(query)) {
      stmt.setString(1, "%" + title + "%");

      var rs = stmt.executeQuery();

      var suggestions = new ArrayList<Suggestion>();

      while (rs.next()) {
        suggestions.add(new Suggestion(
            Optional.of(rs.getInt(SuggestionDbFields.ID.value)),
            rs.getString(SuggestionDbFields.TITLE.value),
            rs.getString(SuggestionDbFields.DESCRIPTION.value),
            rs.getInt(SuggestionDbFields.CATEGORY_ID.value),
            rs.getInt(SuggestionDbFields.AUTHOR_ID.value)));
      }

      return suggestions;
    }
  }

  @Override
  public List<Suggestion> getSuggestions() throws SQLException {
    var query = "SELECT * FROM suggestion";
    var conn = ConnectionManager.getConnection();

    try (var stmt = conn.prepareStatement(query)) {
      var rs = stmt.executeQuery();

      var suggestions = new ArrayList<Suggestion>();

      while (rs.next()) {
        suggestions.add(new Suggestion(
            Optional.of(rs.getInt(SuggestionDbFields.ID.value)),
            rs.getString(SuggestionDbFields.TITLE.value),
            rs.getString(SuggestionDbFields.DESCRIPTION.value),
            rs.getInt(SuggestionDbFields.CATEGORY_ID.value),
            rs.getInt(SuggestionDbFields.AUTHOR_ID.value)));
      }

      return suggestions;
    }
  }

}
