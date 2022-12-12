package com.cookingchef.View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Login extends Application {
	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(Login.class.getResource("hello-view.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
		stage.setTitle("MyChefCook");
		stage.setScene(scene);
		stage.setMaximized(true);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}